Apz.OWASP = function(apz) {
    this.apz = apz;
};
Apz.OWASP.prototype = {
         initialize : function(){
         	org.owasp.esapi.ESAPI.initialize();
         },	
		/****  Validation Methods  ****/
		getValidInput : function(params){
			/* Params contains the below attributes
	         *** val, RegExpName, MaxLength, AllowNull[boolean] ***
	         * Response contains below attributes
	         *** ValidInput, exception ***
	         */
			return $ESAPI.validator().getValidInput("Appzillon", params.val, params.RegExpName, params.MaxLength, params.AllowNull);
		},
		isValidInput : function(params){
			/* Params contains the below attributes
	         *** val, RegExpName, MaxLength, AllowNull[boolean] ***
	         * Response contains below attributes
	         *** true/false ***
	         */
			return $ESAPI.validator().isValidInput("Appzillon", params.val, params.RegExpName, params.MaxLength, params.AllowNull);
		},
		getValidDate : function(params){
			/* Params contains the below attributes
	         *** val, DateFormat, AllowNull[boolean] ***
	         * Response contains below attributes
	         *** ValidInput, exception ***
	         */
			return $ESAPI.validator().getValidDate("Appzillon", params.val , params.DateFormat, params.AllowNull);
		},
		isValidDate : function(params){
			/* Params contains the below attributes
	         *** val, DateFormat, AllowNull[boolean] ***
	         * Response contains below attributes
	         *** true/false ***
	         */
			return $ESAPI.validator().isValidDate("Appzillon", params.val , params.DateFormat, params.AllowNull);
		},
		getValidCreditCard : function(params){
			/* Params contains the below attributes
	         *** val, AllowNull[boolean] ***
	         * Response contains below attributes
	         *** ValidInput, exception ***
	         */
			return $ESAPI.validator().getValidCreditCard("Appzillon", params.val, params.AllowNull);
		},
		isValidCreditCard : function(params){
			/* Params contains the below attributes
	         *** val,  AllowNull[boolean] ***
	         * Response contains below attributes
	         *** true/false ***
	         */
			return $ESAPI.validator().isValidCreditCard("Appzillon", params.val, params.AllowNull);
		},
		getValidNumber : function(params){
			/* Params contains the below attributes
	         *** val, AllowNull[boolean], MinValue, MaxValue***
	         * Response contains below attributes
	         *** ValidInput, exception ***
	         */
			return $ESAPI.validator().getValidNumber("Appzillon", params.val, params.AllowNull, params.MinValue, params.MaxValue);
		},
		isValidNumber : function(params){
			/* Params contains the below attributes
	         *** val, AllowNull[boolean], MinValue, MaxValue***
	         * Response contains below attributes
	         *** true/false ***
	         */
			return $ESAPI.validator().isValidNumber("Appzillon", params.val, params.AllowNull, params.MinValue, params.MaxValue);
		},
		getValidInteger : function(params){
			/* Params contains the below attributes
	         *** val, AllowNull[boolean], MinValue, MaxValue***
	         * Response contains below attributes
	         *** ValidInput, exception ***
	         */
			return $ESAPI.validator().getValidInteger("Appzillon", params.val, params.AllowNull, params.MinValue, params.MaxValue);
		},
		isValidInteger : function(params){
			/* Params contains the below attributes
	         *** val, AllowNull[boolean], MinValue, MaxValue***
	         * Response contains below attributes
	         *** true/false ***
	         */
			return $ESAPI.validator().isValidInteger("Appzillon", params.val, params.AllowNull, params.MinValue, params.MaxValue);
		},
		/****  Encoder Methods  ****/
		cananicalize : function(params){
			/* Params contains the below attributes
	         *** val, strict[boolean] ***
	         * Response contains below attributes
	         *** val ***
	         */
			return $ESAPI.encoder().cananicalize(params.val, params.strict);
		},
		normalize : function(params){
			/* Params contains the below attributes
	         *** val ***
	         * Response contains below attributes
	         *** val ***
	         */
			return $ESAPI.encoder().normalize(params.val);
		},
		encodeForHTML : function(params){
			/* Params contains the below attributes
	         *** val ***
	         * Response contains below attributes
	         *** val ***
	         */
			return $ESAPI.encoder().encodeForHTML(params.val);
		},
		decodeForHTML : function(params){
			/* Params contains the below attributes
	         *** val ***
	         * Response contains below attributes
	         *** val ***
	         */
			return $ESAPI.encoder().decodeForHTML(params.val);
		},
		encodeForHTMLAttribute : function(params){
			/* Params contains the below attributes
	         *** val ***
	         * Response contains below attributes
	         *** val ***
	         */
			return $ESAPI.encoder().encodeForHTMLAttribute(params.val);
		},
		encodeForCSS : function(params){
			/* Params contains the below attributes
	         *** val ***
	         * Response contains below attributes
	         *** val ***
	         */
			return $ESAPI.encoder().encodeForCSS(params.val);
		},
		encodeForJavascript : function(params){
			/* Params contains the below attributes
	         *** val ***
	         * Response contains below attributes
	         *** val ***
	         */
			return $ESAPI.encoder().encodeForJavaScript(params.val);
		},
		encodeForURL : function(params){
			     /* Params contains the below attributes
	         *** val ***
	         * Response contains below attributes
	         *** val ***
	         */
			return $ESAPI.encoder().encodeForURL(params.val);
		},
		decodeFromURL : function(params){
			/* Params contains the below attributes
	         *** val ***
	         * Response contains below attributes
	         *** val ***
	         */
			return $ESAPI.encoder().decodeFromURL(params.val);
		},
		encodeForBase64 : function(params){
			/* Params contains the below attributes
	         *** val ***
	         * Response contains below attributes
	         *** val ***
	         */
			return $ESAPI.encoder().encodeForBase64(params.val);
		},
		decodeFromBase64 : function(params){
			/* Params contains the below attributes
	         *** val ***
	         * Response contains below attributes
	         *** val ***
	         */
			return $ESAPI.encoder().decodeFromBase64(params.val);
		},
		/****  HTTP Utility Methods  ****/
		addCookie : function(params){
			/* Params contains the below attributes
	         *** name,val ***
	         */
		    $ESAPI.httpUtilities().addCookie(org.owasp.esapi.net.Cookie(params.name,params.val));
		},
		getCookie : function(params){
			/* Params contains the below attributes
	         *** name ***
	         * Response contains below attributes
	         *** cookie/null ***
	         */
			return $ESAPI.httpUtilities().getCookie(params.name); 
		},
		killCookie : function(params){
			/* Params contains the below attributes
	         *** name ***
	         * Response contains below attributes
	         *** true/false ***
	         */
			return $ESAPI.httpUtilities().killCookie(params.name);
		},
		killAllCookies : function(){
			return $ESAPI.httpUtilities().killAllCookies();
		},
		getRequestParameter : function(params){
			/* Params contains the below attributes
	         *** name ***
	         * Response contains below attributes
	         *** String val ***
	         */
			return $ESAPI.httpUtilities().getRequestParameter(params.name);
		}
};
