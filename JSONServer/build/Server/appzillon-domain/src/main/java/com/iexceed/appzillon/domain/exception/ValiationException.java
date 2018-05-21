package com.iexceed.appzillon.domain.exception;

import com.iexceed.appzillon.exception.AppzillonException;

/**
 *
 * @author arthanarisamy A new Validation exception is created to validate the
 * interface master requests
 */
// Server Appzillon  �RS Ref�  Changes  (Server Appzillon  2.1 ) - Start
public class ValiationException extends AppzillonException {

    private static final long serialVersionUID = -6137691840498075830L;

    public enum c_exCode {

        VAL_EX_001, VAL_EX_002, VAL_EX_003, VAL_EX_004, VAL_EX_005, VAL_EX_006, VAL_EX_007, VAL_EX_008, VAL_EX_009, VAL_EX_010, VAL_EX_011, VAL_EX_012, VAL_EX_013, VAL_EX_014, VAL_EX_015, VAL_EX_016, VAL_EX_017, VAL_EX_018;

        public String toString() {
            return this.name().replace('_', '-');
        }
    }
    String code;
    String message;

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
	// Server Appzillon  RS Ref  Changes  (Server Appzillon  2.1 ) - End
}
