package com.example.ipotracker

import com.example.ipotracker.presentation.applicationinfo.generateFormPrintText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DematAccountFormTest {

    @Test
    fun testFormPrintIncludesBankDetailsWhenSelectedYes() {
        val formText = generateFormPrintText(
            applicantName = "Rajesh Sharma",
            panNumber = "ABCDE1234F",
            applyForIpo = true,
            depositoryType = "CDSL",
            addBankDetails = true,
            bankName = "HDFC Bank",
            bankAccountNumber = "123456789012",
            upiId = "rajesh@hdfcbank"
        )

        assertTrue(formText.contains("Applicant Name : Rajesh Sharma"))
        assertTrue(formText.contains("PAN Number     : ABCDE1234F"))
        assertTrue(formText.contains("Depository     : CDSL"))
        assertTrue(formText.contains("Bank Name      : HDFC Bank"))
        assertTrue(formText.contains("Bank A/C No.   : 123456789012"))
        assertTrue(formText.contains("UPI ID         : rajesh@hdfcbank"))
    }

    @Test
    fun testFormPrintExcludesBankDetailsWhenSelectedNo() {
        val formText = generateFormPrintText(
            applicantName = "Anita Verma",
            panNumber = "PQRST5678G",
            applyForIpo = true,
            depositoryType = "NSDL",
            addBankDetails = false,
            bankName = "State Bank of India",
            bankAccountNumber = "987654321098",
            upiId = "anita@sbi"
        )

        assertTrue(formText.contains("Applicant Name : Anita Verma"))
        assertTrue(formText.contains("PAN Number     : PQRST5678G"))
        assertTrue(formText.contains("Depository     : NSDL"))
        assertFalse(formText.contains("Bank Name"))
        assertFalse(formText.contains("Bank A/C No."))
        assertFalse(formText.contains("State Bank of India"))
        assertFalse(formText.contains("987654321098"))
        assertTrue(formText.contains("UPI ID         : anita@sbi"))
    }

    @Test
    fun testAccountNumberNumericOnly() {
        val validAccount = "50100412345678"
        val invalidAccount = "501004123AB678"

        assertTrue(validAccount.all { it.isDigit() })
        assertFalse(invalidAccount.all { it.isDigit() })
    }

    @Test
    fun testPanNumberRegexFormat() {
        val panRegex = Regex("^[A-Z]{5}[0-9]{4}[A-Z]$")

        assertTrue("ABCDE1234F".matches(panRegex))
        assertFalse("ABC1234".matches(panRegex))
        assertFalse("12345ABCDE".matches(panRegex))
    }
}
