Apz.Val = function(apz) {
   ////Core Instance
   this.apz = apz;
   this.gblockdata = [];
};
Apz.Val.prototype = {
   validateScreen : function(scr) {
	   /* Params contains the below value
	       *** scrName ***
	   */
      var lresult = true;
      var lerror = "";
      var lerrdet = {};
      var lscrdiv = "scr__" + this.apz.currAppId + '__' + scr + "__main";
      var lerrors = new Array();
      var myObj = this;
      var elmsValue = this.apz.scrMetaData.elms;
      $("#" + lscrdiv + " input").each(function() {
         if (this.type == "radio") {
         } else if (this.classList.contains('pagination-input')) {
         } else if (this.type == "checkbox") {
         } else if ($(this).closest("tr").hasClass("ssp")) {
         } else {
            lerror = myObj.validateInputAct(this, false);
            if (lerror != false) {
               lerrdet = {}, lerrdet.error = lerror;
               lerrdet.element = this;
               lerrors[lerrors.length] = lerrdet;
            }
            var lid = this.getAttribute("id");
            if (lerrors.length > 0) {
               lresult = false;
            }
         }
      });
      $("#" + lscrdiv + " select").each(function() {
         if ($(this).closest("tr").hasClass("ssp")) {
         } else {
            lerror = myObj.validateInputAct(this, false);
            if (lerror != false) {
               lerrdet = {}, lerrdet.error = lerror;
               lerrdet.element = this;
               lerrors[lerrors.length] = lerrdet;
            }
            var lid = this.getAttribute("id");
            if (lerrors.length > 0) {
               lresult = false;
            }
         }
      });
 for (var i = 0; i < elmsValue.length; i++) {
        if (elmsValue[i].custom != "N") {
          var custObj = this.apz[elmsValue[i].type];
          var custDomObject = document.getElementById(elmsValue[i].id);
          if (!this.apz.isNull(custDomObject) && $(custDomObject).is(":visible") && $(custDomObject).css('display') !== 'none' && !$(custDomObject).closest("tr").hasClass("showspace")) {
            if (custObj && this.apz.isFunction(custObj.validateObj)) {
              lerror = custObj.validateObj(custDomObject, elmsValue[i]);
              if (!this.apz.isNull(lerror) && lerror != false) {
                 lerrdet = {}, lerrdet.error = lerror;
                 lerrdet.element = custDomObject;
                 lerrors[lerrors.length] = lerrdet;
              }
            }
          } else {
            var customErrors = [];
            var containerName = elmsValue[i].container;
            var containerData = myObj.apz.scrMetaData.containersMap[containerName];
            if (containerData.multiRec == "Y") {
              var obj = {
                "totalRecs": containerData.totalRecs,
                "pageSize": containerData.pageSize,
                "currentPage": containerData.currPage,
                "totalPages": containerData.totalPages
              };
              if (obj.pageSize == 999) {
                customErrors = this.validateCustomElements(obj.totalRecs, elmsValue[i], custObj);
              } else {
                  if (obj.currentPage > 0 && obj.currentPage == obj.totalPages) {
                    var fixedPageSize = obj.totalRecs % obj.pageSize;
                    fixedPageSize = (fixedPageSize==0)?1:fixedPageSize;
                    customErrors = this.validateCustomElements(fixedPageSize, elmsValue[i], custObj);
                  } else {
                    customErrors = this.validateCustomElements(obj.totalRecs, elmsValue[i], custObj);
                  }
                }
              }
              lerrors.concat(customErrors);
            }
          }
        }
        if (lerrors.length > 0) {
           lresult = false;
        }
      return lresult;
   },validateCustomElements : function(totalRecs,element,custObj){
      var lresult = true, lerror = "";
      var custDomObject, lerrdet = {}, lerrors = [];
      for (var i = 0; i < totalRecs; i++) {
        custDomObject = document.getElementById(element.id + "_" + i);
        if (!this.apz.isNull(custDomObject) && $(custDomObject).is(":visible") && $(custDomObject).css('display') !== 'none' && !$(custDomObject).closest("tr").hasClass("showspace")) {
          if (custObj && this.apz.isFunction(custObj.validateObj)) {
            lerror = custObj.validateObj(custDomObject, element);
            if (!this.apz.isNull(lerror) && lerror != false) {
               lerrdet = {}, lerrdet.error = lerror;
               lerrdet.element = custDomObject;
               lerrors[lerrors.length] = lerrdet;
            }
          }
        }
      }
      return lerrors;
   },validateControl: function(params) {
       ///Expects id, message and/or code
       var objId = params.id;
         var pinput = $("#"+objId)[0];
         var errMsg = this.validateInputAct(pinput, false);
        $input = $(pinput);
        var lid = this.apz.getObjIdWORowNumber(pinput);
         var elmObj = this.apz.scrMetaData.elmsMap[lid];
        var $inputParent = $input.parent();
        if (!this.apz.isNull(elmObj) && elmObj.type=="DROPDOWN" && pinput.tagName=="INPUT") {
         $inputParent = $input.parent().parent();
        }
         if (!apz.isNull(errMsg)) {
            if(!apz.isNull(params.message)){
               desc = params.message;
            } else if (!apz.isNull(params.code)){
               desc = apz.msgs[this.apz.currAppId][params.code];
               desc = desc.substring(1);
            } else {
               errMsg = apz.msgs[this.apz.currAppId][errMsg];
               desc = errMsg.substring(1);
            }
            if($input.closest("ul").hasClass("hrow")){
              $input.closest("li").addClass("vcn");
            } else {
              $input.parents('.srb').addClass("vcn");
            }
            $input.addClass("err");
            $inputParent.children(".vtx").remove();
            $inputParent.append('<p class="vtx">' + desc + '</p>');
         } else {
           if($input.closest("ul").hasClass("hrow")){
              $input.closest("li").removeClass("vcn");
           } else {
              $input.parents('.srb').removeClass("vcn");
           }
          if($inputParent[0].lastChild.tagName == "P"){
            $inputParent[0].removeChild($inputParent[0].lastChild);
          }
        }
   }, validateContainer :function(contId) {
	   /* Params contains the below value
	       *** contId ***
	   */
      var lresult = true;
      var lerror = "";
      var lerrdet = {};
      var lerrors = new Array();
      var myObj = this;
      $("#" + contId + " input").each(function() {
         if (this.type == "radio") {
         } else if (this.classList.contains('pagination-input')) {
         } else if (this.type == "checkbox") {
         } else if ($(this).closest("tr").hasClass("ssp")) {
         } else {
            lerror = myObj.validateInputAct(this, false);
            if (lerror != false) {
               lerrdet = {}, lerrdet.error = lerror;
               lerrdet.element = this;
               lerrors[lerrors.length] = lerrdet;
            }
            var lid = this.getAttribute("id");
            if (lerrors.length > 0) {
               lresult = false;
            }
         }
      });
      $("#" + contId + " select").each(function() {
         if ($(this).closest("tr").hasClass("ssp")) {
         } else {
            lerror = myObj.validateInputAct(this, false);
            if (lerror != false) {
               lerrdet = {}, lerrdet.error = lerror;
               lerrdet.element = this;
               lerrors[lerrors.length] = lerrdet;
            }
            var lid = this.getAttribute("id");
            if (lerrors.length > 0) {
               lresult = false;
            }
         }
      });
      return lresult;
   }, validateInput : function(pinput) {
      return this.validateInputAct(pinput, true);
   }, validateInputAct : function(pinput, ponline) {
      var lresult = true;
      var lerror = "";
      if (!this.apz.isNull(pinput) && $(pinput).is(":visible") && !$(pinput).closest("tr").hasClass("ssp")) {
         //Initializing the values type and mandatory flag
         var lid = this.apz.getObjIdWORowNumber(pinput);
         var elmObj = this.apz.scrMetaData.elmsMap[lid];
         if(elmObj !== undefined){ 
         var ldtype = "STRING";
         var lmandatory = "N";
         var isCustom = "N";
         if (!this.apz.isNull(elmObj)) {
            ldtype = elmObj.dataType;
            lmandatory = elmObj.mand;
            isCustom = elmObj.custom;
         }
         if(isCustom == "Y"){
            var custObj = this.apz[elmObj.type];
            if(custObj && this.apz.isFunction(custObj.validateObj)){
              lerror = custObj.validateObj(pinput,elmObj);
            }
         } else {
           var inputVal = pinput.value;
           if (!this.apz.isNull(elmObj) && elmObj.type=="DROPDOWN") {
              inputVal = this.apz.getObjValue(pinput);
           }
           if (!this.apz.isNull(inputVal) && !this.apz.isNull(elmObj)) {
              if ((ldtype == "NUMBER" || ldtype == "INTEGER")) {
                 lerror = this.validateNumberObj(pinput,elmObj.displayAsLiteral);
              } else if (ldtype == "DATE") {
                 lerror = this.validateDateObj(pinput);
              } else if (ldtype == "DATETIME") {
                 lerror = this.validateDateTimeObj(pinput);
              } else {
                 lerror = this.validateStringObj(pinput);
              }
           } else {
              if (lmandatory == "Y") {
                 lresult = false;
                 lerror = "APZ-VAL-001";
              }
           }
         }
         // //Result Handling....
         if (lerror != "") {
            if (!$(pinput).parent()[0].classList.contains("ssp")) {
               this.addClass(pinput);
               if (!this.apz.isNull(elmObj) && elmObj.type=="DROPDOWN") {
                  $(pinput).parent('div').addClass('err');
               }
               if (ponline == true) {
                  var params = {"code":lerror}
                  this.apz.dispMsg(params);
               }
            }
         } else {
            this.removeClass(pinput);
            if (!this.apz.isNull(elmObj) && elmObj.type=="DROPDOWN") {
               $(pinput).parent('div').removeClass('err');
            }
         }
        }
      }
      return lerror;
   }, addClass : function(pobj) {
      var ltagname = pobj.tagName;
      if (ltagname == "INPUT") {
         var lid = pobj.id;
         var ltype = $('#' + lid).attr('type');
         if (ltype == "hidden") {
            if (pobj.classList.contains("appzillon_date")) {
               var lclasslist = document.getElementById(lid).nextSibling.nextElementSibling;
               lclasslist.className = lclasslist.className + "  err  ";
            }
         } else if (ltype == "checkbox") {
         } else {
            $(pobj).addClass("err");
         }
      } else if ((ltagname == "LI") || (ltagname == "SELECT") || (ltagname == "TEXTAREA") || (ltagname == "DD") || (ltagname == "P") || (ltagname == "SPAN") || (ltagname == "A")) {
         $(pobj).addClass("err");
      }
   }, removeClass : function(pobj) {
      var ltagname = pobj.tagName;
      if (ltagname == "INPUT") {
         var lid = pobj.id;
         var ltype = $('#' + lid).attr('type');
         if (ltype == "hidden") {
            if (pobj.classList.contains("appzillon_date")) {
               var lclasslist = document.getElementById(lid).nextSibling.nextElementSibling;
               lclasslist.className = lclasslist.className.replace("err", '');
            }
         } else if (ltype == "checkbox") {
         } else {
            $(pobj).removeClass("err");
         }
      } else if ((ltagname == "LI") || (ltagname == "SELECT") || (ltagname == "TEXTAREA") || (ltagname == "DD") || (ltagname == "P") || (ltagname == "SPAN") || (ltagname == "A")) {
         $(pobj).removeClass("err");
      }
   }, validateStringObj : function(pobj) {
	   /* Params contains the below value
	       *** obj(DOM element) ***
	       * Response contains below value
	       *** boolean ***
	   */
      var lerror = "";      
      var lvalue = this.apz.getObjValue(pobj);
      lerror = this.validateString(pobj, lvalue);
      return lerror;
   }, validateString : function(pobj, pval) {
      var lerror = "";
      var checkvalString = true;
      var lid = this.apz.getObjIdWORowNumber(pobj);
      if(this.apz.isFunction(this.apz.app.preValidateString)){
         checkvalString = this.apz.app.preValidateString(pval);
         if (this.apz.isNull(checkvalString)) {
            checkvalString = true;
         }
      }
      if(checkvalString){
         var lresult = true;
         var lelemobj = this.apz.scrMetaData.elmsMap[lid];
         if (!this.apz.isNull(lelemobj)) {
            var lmaxlength = lelemobj.maxLen;
            var lminlength = lelemobj.minLen;
            var lpattern = lelemobj.pattern;
            if (!this.apz.isNull(pval)) {
               if (lresult == true) {
                  if (!this.apz.isNull(lmaxlength)) {
                     if (pval.length > lmaxlength) {
                        lresult = false;
                        lerror = "APZ-VAL-002";
                     }
                  }
               }
               if (lresult == true) {
                  if (!this.apz.isNull(lminlength)) {
                     if (pval.length < lminlength) {
                        lresult = false;
                        lerror = "APZ-VAL-003";
                     }
                  }
               }
               //Pattern Provided by user will get highest priority
               if (lresult == true) {
                  if (!this.apz.isNull(lpattern)) {
                     lpattern = this.apz.replaceAll({"string":lpattern,"key":'/',"replaceData":''});
                     var lregex = new RegExp(lpattern);
                     try {
                        if (!lregex.test(pval)) {
                           lresult = false;
                           lerror = "APZ-CNT-128";
                        }
                     } catch (err) {
                        lresult = false;
                        lerror = "APZ-CNT-128";
                     }
                  }
               }
            }
         }
      }
      if(this.apz.isFunction(this.apz.app.postValidateString)){
         this.apz.app.postValidateString(pobj);
      }
      return lerror;
   }, validateNumberObj : function(pobj,displayAsLiteral) {
	   /* Params contains the below values
	       *** obj(DOM element), displayAsLiteral(Y/N) ***
	       * Response contains below value
	       *** boolean ***
	   */
      var lerror = "";
      var lvalue = this.apz.getObjValue(pobj);
      lerror = this.validateNumber(pobj, lvalue,displayAsLiteral);
      return lerror;
   }, validateNumber : function(pobj, pval,displayAsLiteral) {
      var lerror = "";
      var lresult = true;
      var lcheckvalnumber = true;
      var lid = this.apz.getObjIdWORowNumber(pobj);
      if(this.apz.isFunction(this.apz.app.preValidateNumber)){
         lcheckvalnumber = this.apz.app.preValidateNumber(pobj);
         if (this.apz.isNull(lcheckvalnumber)) {
            lcheckvalnumber = true;
         }
      }
      if (lcheckvalnumber) {
      var lelemobj = this.apz.scrMetaData.elmsMap[lid];
      if (!this.apz.isNull(lelemobj)) {
         var lminval = lelemobj.minVal;
         var lmaxval = lelemobj.maxVal;
         var ldtype = lelemobj.dataType;
         if (!this.apz.isNull(pval)) {
            //// Checking for Number or Integer
            if (ldtype == "NUMBER") {
               lresult = this.isNumber(pval,displayAsLiteral);
            } else {
               lresult = this.isInt(pval,displayAsLiteral);
            }
            if (!lresult) {
               lerror = "APZ-VAL-010";
               lresult = "false";
            } else {
               var lmaxval = parseInt(lmaxval);
               var lminval = parseInt(lminval);
            }
         }
         if (lresult == true) {
            if (!this.apz.isNull(lminval)) {
               var params = {};
               params.value = pval;
               params.decimalSep = this.apz.decimalSep;
               if (this.apz.unFormatNumber(params) < lminval) {
                  lresult = false;
                  lerror = "APZ-VAL-006";
               }
            }
         }
         if (lresult == true) {
            if (!this.apz.isNull(lmaxval)) {
               var params = {};
               params.value = pval;
               params.decimalSep = this.apz.decimalSep;
               if (this.apz.unFormatNumber(params) > lmaxval) {
                  lresult = false;
                  lerror = "APZ-VAL-005";
               }
            }
         }
      }
   }
    if(this.apz.isFunction(this.apz.app.postValidateNumber)){
         this.apz.app.postValidateNumber(pobj);
      }
      return lerror;
   }, validateDateObj : function(pobj) {
	   /* Params contains the below value
	       *** obj(DOM element) ***
	       * Response contains below value
	       *** boolean ***
	   */
      var lerror = "";     
      var lvalue = this.apz.getObjValue(pobj);
      lerror = this.validateDate(pobj, lvalue);
      return lerror;
   }, validateDate : function(pobj, pval) {
      var lerror = "";
      var checkValDate = true;
      if(this.apz.isFunction(this.apz.app.preValidateDate)){
         checkValDate = this.apz.app.preValidateDate(pobj);
         if (this.apz.isNull(checkValDate)) {
            checkValDate = true;
         }
      }
      if(checkValDate){
         var lresult = true;
         if (!this.apz.isNull(pval)) {
            var luserformat = this.apz.dateFormat;
            var lcheckdate = this.isDate(pval, luserformat);
            if (!lcheckdate) {
               lresult = false;
               lerror = "APZ-VAL-008";
            }
         }
      }
      if(this.apz.isFunction(this.apz.app.postValidateDate)){
         this.apz.app.postValidateDate(pobj);
      }
      return lerror;
   }, validateDateTimeObj : function(pobj) {
	   /* Params contains the below value
	       *** obj(DOM element) ***
	       * Response contains below value
	       *** boolean ***
	   */
      var lerror = "";
      var lvalue =  this.apz.getObjValue(pobj);
      lerror = this.validateDateTime(pobj, lvalue);
      return lerror;
   }, validateDateTime : function(pobj, pval) {
      var lerror = "";
      var lresult = true;
      var lcheckvaldate = true;
      if(this.apz.isFunction(this.apz.app.preValidateDateTimeObj)){
         lcheckvaldate = this.apz.app.preValidateDateTimeObj(pobj);
         if (this.apz.isNull(lcheckvaldate)) {
            lcheckvaldate = true;
         }
      }
      if (lcheckvaldate) {
         if (!this.apz.isNull(pval)) {
            var luserformat = this.apz.dateTimeFormat;
            var lcheckdate = this.isDate(pval, luserformat);
            if (!lcheckdate) {
               lresult = false;
               lerror = "APZ-VAL-008";
            }
         }
      }
      if(this.apz.isFunction(this.apz.app.postValidateDateTimeObj)){
         this.apz.app.postValidateDateTimeObj(pobj);
      }
      return lerror;
   }, isInt : function(pvalue,displayAsLiteral) {
      var lchecknumber = this.isNumber(pvalue,displayAsLiteral);
      var lerror = false;
      var lvalue = pvalue;
      var linteger = '';
      if (lchecknumber) {
         linteger = lvalue.toString().indexOf('.');
         if (linteger === -1) {
            lerror = true;
         }
      }
      return lerror;
   }, isFloat : function(pvalue,displayAsLiteral) {
      var lchecknumber = this.isNumber(pvalue,displayAsLiteral);
      var lerror = false;
      var lvalue = pvalue;
      var lfloat = '';
      if (lchecknumber) {
         lfloat = lvalue.toString().indexOf('.');
         if (lfloat != -1) {
            lerror = true;
         }
      }
      return lerror;
   }, isNumber : function(pvalue,displayAsLiteral) {
      var params = {};
      params.value = pvalue;
      params.decimalSep = this.apz.decimalSep;
      params.displayAsLiteral = displayAsLiteral;
      var lval =  this.apz.unFormatNumber(params);
      var lerror = false;
      try {
         if ($.isNumeric(lval)) {
            lerror = true;
         }
      } catch (err) {
         lerror = false;
      }
      return lerror;
   }, isString : function(pvalue) {
      var lerror = false;
      if ($.type(pvalue).toLowerCase() == "string") {
         lerror = true;
      }
      return lerror;
   }, isDate : function(pvalue, pdateformat) {
      var lerror = false;
      try {
        var dateValueArr = pvalue.split(" - ");
        if(dateValueArr.length == 2) {
          var ldatecheck1 = Date.parseExact(dateValueArr[0], pdateformat);
          var ldatecheck2 = Date.parseExact(dateValueArr[1], pdateformat);
          if (!this.apz.isNull(ldatecheck1) && !this.apz.isNull(ldatecheck2)) {
            lerror = true;
         }
        } else {
          var ldatecheck = Date.parseExact(pvalue, pdateformat);
          if (!this.apz.isNull(ldatecheck)) {
            lerror = true;
         }
        }
      } catch (err) {
         lerror = false;
      }
      return lerror;
   },
   // //////////////// Validate the Password //////////
   validatePassword : function(args) {
      //Expects newPassword,confirmPassword,oldPassword
	   /* Params contains the below attributes
	       *** oldPassword, newPassword, confirmPassword ***
	       * Response contains below attributes
	       *** boolean ***
	   */
      var lreturn = true;
         var params = {};
      if (this.apz.isNull(args.newPassword)) {
         // //if new password is null.
         params.code = "APZ-SVR-PNL";
         this.apz.dispMsg(params);
         lreturn = false;
      } else if (this.apz.isNull(args.confirmPassword)) {
         // //if confirm password is null.
         params.code = "APZ-SVR-PNL";
         this.apz.dispMsg(params);
         lreturn = false;
      } else if (this.apz.isNull(args.oldPassword)) {
         // // if old password is null.
         params.code = "APZ-SVR-PNL";
         this.apz.dispMsg(params);
         lreturn = false;
      } else {
         if (args.oldPassword == args.newPassword) {
            // //if old password and new password are same.
            params.code = "APZ-PSW-RULE";
            this.apz.dispMsg(params);
            lreturn = false;
         } else if (args.newPassword != args.confirmPassword) {
            // //if new password and confirm password are not same.
            params.code = "APZ-SVR-PNC";
            this.apz.dispMsg(params);
            lreturn = false;
         }
      }
      return lreturn;
   }
}