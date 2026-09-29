# Implementation Plan: IPODekho Enterprise Platform & Automated Real-Data Pipeline

This plan defines the end-to-end architecture and implementation for **IPODekho**, fulfilling all 40 enterprise requirements, integrating a **fully automated scraper** that pulls real IPO and GMP data directly from NSE, BSE, and GMP portals every 15–30 minutes, and **100% preserving the existing Android app UI**.

---

## 1. High-Level Data Flow & Architecture

```
   ┌────────────────────────────────────────────────────────┐
   │             REAL-TIME DATA SOURCES (AUTOMATIC)          │
   │   - NSE India (Live Public IPO Bidding & Book Building)│
   │   - BSE India (Mainboard & SME Equity IPO Feeds)       │
   │   - Chittorgarh & InvestorGain (Live GMP & Ratings)    │
   │   - Link Intime & KFintech (Allotment Status Endpoints)│
   └───────────────────────────┬────────────────────────────┘
                               │
                               │ HTTPS (Every 15-30 mins during market hours)
                               ▼
   ┌────────────────────────────────────────────────────────┐
   │    AUTOMATED BACKGROUND WORKER (IHostedService / Hangfire)│
   │    - Resilient HttpClient with User-Agent & Cookie Pool │
   │    - HtmlAgilityPack & JSON Parsing Pipeline           │
   │    - Data Sanitizer & Transactional Upsert             │
   └───────────────────────────┬────────────────────────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │    MS SQL SERVER    │
                    │ (Database-First)    │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │        DAL          │
                    │ EF Core / Entities  │
                    │ Repositories        │
                    │ Unit of Work        │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │        BAL          │
                    │ Business Services   │
                    │ Business Rules      │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │     REST API        │
                    │ ASP.NET Core 8      │
                    │ Controllers         │
                    │ JWT Auth / Roles    │
                    └───────┬─────┬───────┘
                            │     │
                  ┌─────────┘     └─────────┐
                  ▼                         ▼
        ┌──────────────────┐      ┌───────────────────────────┐
        │   WEB APP        │      │        MOBILE APP         │
        │ ASP.NET Core MVC │      │ Android (Jetpack Compose) │
        │ Razor / Bootstrap│      │  [UI 100% UNCHANGED]      │
        │ Admin + User     │      │ User Experience           │
        └──────────────────┘      └───────────────────────────┘
```

> **Non-Negotiable Constraints**:
> 1. Web and Mobile **NEVER** connect directly to SQL Server; both consume `IPODekho.API`.
> 2. Android UI remains **100% identical** (no composables, screens, or layouts modified).

---

## 2. Multi-Layer Solution (`IPODekho.sln`)

The solution contains distinct, decoupled projects:

```
backend-aspnetcore/
├── IPODekho.sln
│
├── src/
│   ├── IPODekho.Domain/                  # Entities, Enums (MAINBOARD, SME), Constants
│   │
│   ├── IPODekho.DAL/                     # Database-First EF Core 8 & Repositories
│   │   ├── Context/                      # IPODekhoDbContext (Scaffolded from SQL Server)
│   │   ├── Repositories/                 # GenericRepository<T>, IpoRepository, GmpRepository, etc.
│   │   ├── UnitOfWork/                   # IUnitOfWork & UnitOfWork
│   │   └── Database/                     # Complete DDL SQL schema scripts
│   │
│   ├── IPODekho.BAL/                     # Business Access Layer
│   │   ├── Interfaces/                   # IIpoService, IGmpService, ISubscriptionService, IScraperService
│   │   ├── Services/                     # Business services implementing rules, sorting, filtering
│   │   └── DTOs/                         # IPOListDto, IPODetailDto, GmpDto, SubscriptionDto, AuthDto
│   │
│   ├── IPODekho.Scraper/                 # Background Automated Worker Service
│   │   ├── Crawlers/                     # NseCrawler, BseCrawler, GmpCrawler, RegistrarCrawler
│   │   └── Jobs/                         # IpoSyncBackgroundWorker (Runs every 15-30 mins)
│   │
│   ├── IPODekho.API/                     # ASP.NET Core 8 REST API
│   │   ├── Controllers/                  # IpoController, GmpController, AuthController, AdminController
│   │   ├── Middleware/                   # GlobalExceptionMiddleware, RequestLoggingMiddleware
│   │   └── Swagger/                      # Swagger/OpenAPI with JWT Bearer auth
│   │
│   ├── IPODekho.Web/                     # ASP.NET Core 8 MVC Application
│   │   ├── Areas/
│   │   │   ├── Admin/                    # Admin Dashboard, Scraper Monitor, Audit Logs
│   │   │   └── User/                     # User Portfolio, Watchlist
│   │   ├── Controllers/                  # HomeController, IpoController, AccountController
│   │   └── Views/                        # Razor Views with Bootstrap 5 & Chart.js
│   │
│   ├── IPODekho.Shared/                  # Common ApiResponse<T>, PaginatedResponse<T>
│   │
│   └── IPODekho.Tests/                   # xUnit Unit & Integration Tests
```

---

## 3. Real Data Automated Scraper Pipeline

### Sources & Frequencies:
- **NSE / BSE India Feeds**: Scrapes active IPO bidding tables (QIB, NII, Retail subscription times) every 15 minutes between 10:00 AM – 5:00 PM IST on trading days.
- **Chittorgarh & InvestorGain**: Scrapes live Grey Market Premium (GMP), Kostak rates, Subject to Sauda, and expected listing price every 30 minutes.
- **Registrar Portals (Link Intime & KFintech)**: Auto-checks allotment status links once allotment dates arrive.

### Scraper Architecture:
1. `IHttpClientFactory` with rotating user-agents, automated cookie handling, and exponential backoff retry via Polly.
2. `HtmlAgilityPack` parses tables into domain models.
3. Transactional upsert:
   - If an IPO does not exist -> Insert new record.
   - If an IPO exists -> Update price band, dates, and subscription times.
   - Insert new timestamped snapshot in `GMPHistory` table for chart rendering.

---

## 4. Database-First Workflow & Scaffold-DbContext

### Database Schema Script (`schema.sql`)
Creates:
- `Users`, `Roles`, `UserRoles`
- `Companies`, `Registrars`, `LeadManagers`
- `IPOs`, `IPOCategories`, `IPODates`
- `SubscriptionDetails`, `SubscriptionCategories`
- `GMPDetails`, `GMPHistory`
- `FinancialDetails`, `AllotmentDetails`
- `Watchlists`, `Notifications`, `AuditLogs`, `SystemSettings`

### Exact Scaffold-DbContext Command
```bash
dotnet ef dbcontext scaffold "Server=localhost,1433;Database=IPODekho;User Id=sa;Password=YourStrong@Password123;TrustServerCertificate=True;" \
    Microsoft.EntityFrameworkCore.SqlServer \
    --project src/IPODekho.DAL \
    --startup-project src/IPODekho.API \
    --output-dir Models \
    --context-dir Context \
    --context IPODekhoDbContext \
    --data-annotations \
    --force
```

---

## 5. Security & Role-Based Authorization

- **JWT Authentication**: Secure Bearer tokens with 24-hour expiration and refresh token support.
- **Role Enforcement**:
  - `ADMIN`: Has full CRUD permissions over IPOs, GMPs, User management, Scraper trigger, and Audit logs via `[Authorize(Roles="ADMIN")]`.
  - `USER`: Has read permissions, search, filter, watchlist bookmarking, profit calculator, and notifications.

---

## 6. Android Mobile Client Integration (Preserving UI 100%)

- **Zero UI Disruption**: No changes to Compose screens, themes, layouts, or user journey.
- **Data Layer Hook**: The existing Retrofit interface in `IpoApiService.kt` and `IpoConfig.kt` connects directly to the new API endpoints (`/api/ipo`, `/api/ipo/{id}`, `/api/ipo/{id}/gmp`, etc.).
- When the background scraper updates SQL Server, both the **Web MVC application** and the **Android app** immediately see the live data.

---

## 7. Deliverables & Execution Roadmap

1. **Step 1**: Generate MS SQL Server database script (`schema.sql`) with all tables, constraints, and baseline indices.
2. **Step 2**: Implement the multi-project Visual Studio solution (`IPODekho.Domain`, `IPODekho.DAL`, `IPODekho.BAL`, `IPODekho.Scraper`, `IPODekho.API`, `IPODekho.Web`, `IPODekho.Shared`, `IPODekho.Tests`).
3. **Step 3**: Configure the 15–30 minute automated scraper worker (`IpoSyncBackgroundWorker`).
4. **Step 4**: Build the MVC Web application with Razor views, Bootstrap 5, and Chart.js.
5. **Step 5**: Verify Android App connection and build integrity (`compile_applet`).
6. **Step 6**: Provide comprehensive `README.md` with execution, Docker, and deployment instructions.
