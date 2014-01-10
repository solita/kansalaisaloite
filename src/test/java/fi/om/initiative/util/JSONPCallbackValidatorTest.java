package fi.om.initiative.util;

import static junit.framework.Assert.*;

import org.junit.Test;

import fi.om.initiative.validation.JSONPCallbackValidator;

public class JSONPCallbackValidatorTest {

    @Test
    public void Valid_Callbacks() {
        isValid("hello");
        isValid("foo23");
        isValid("$210");
        isValid("Ääkköset");
        isValid("$.ajaxHandler");
        isValid("array_of_functions[42]");
        isValid("array_of_functions[42].method");
        isValid("array_of_functions[42][5]");
        isValid("$.ajaxHandler[42][1].foo");
    }

    @Test
    public void Invalid_Callbacks() {
        isNotValid("");
        isNotValid("alert()");
        isNotValid("a-b");
        isNotValid("23foo");
        isNotValid("\u0020");
        isNotValid(" somevar");
        isNotValid("$.23");
        isNotValid("array_of_functions[42]foo[1]");
        isNotValid("empty_array[]");
        isNotValid("array_of_functions[\"key\"]");
        isNotValid("array_of_functions[key]");
        
        // Reserved words
        isNotValid("synchronized");
        isNotValid("$.if");
        isNotValid("array[5].else");
    }
    
    private void isValid(String callback) {
        assertTrue(callback + " should validate", JSONPCallbackValidator.isValidJSONPCallback(callback));
    }
    
    private void isNotValid(String callback) {
        assertFalse(callback + " should not validate", JSONPCallbackValidator.isValidJSONPCallback(callback));
    }
}
