Apz.Ns = function(apz) {
   ////Core Instance
   this.apz = apz;
   this.safeToken = "";
   this.sessionToken = "";
   this.serverNonce = "";
   this.counter = 0;
   this.publicKey = "";
   ///Add Random Number
   require.config({
		urlArgs: "bust=" + (new Date()).getTime()
   });

};
///////////////////Prototype Definition///////////////////////
Apz.Ns.prototype = {
   callNative : function(req) {
        try {
         var res = {};
            res.id = req.id;
            var params = {"message":"Plugin not supported for Simulator"};
            params.callBack = req.callBack;
            params.callBackObj = req.callBackObj;
            this.apz.dispMsg(params);
            //this.apz.initNativeService(res);
            //Apz.nativeServiceCB(res);
        } catch (e) {
            console.log(e);
        }
   }, changePassword : function(req) {
      var params = req.params;
      var changePwdReq = params.req.changePasswordRequest;
      changePwdReq.newPassword = apzIde.encryptData(changePwdReq.newPassword);
      if (this.apz.authenticationType == "#DeviceId") {
         try {
            changePwdReq.pwd = apzIde.hashPwd(changePwdReq.userId, changePwdReq.pwd, this.apz.serverToken, this.apz.deviceId, changePwdReq.sysDate);
         } catch (e) {
            alert("Hashing Failed in Change Password");
         }
      }
      this.sendReq(req);
   }, log : function(msg,type) {
      if ( typeof apzIde !== "undefined") {
         apzIde.log(msg);
      } else {
         console.log(msg);
      }
   }, login : function(req) {
      if(this.apz.isNull(req.params.bioMetAuth) && req.params.bioMetAuth === "Y"){
         this.callNative(req);
      } else {
         if (this.apz.authenticationType == "#DeviceId") {
            var params = req.params;
            var hash = params.pwd;
            try {
               hash = apzIde.hashPwd(params.userId, params.pwd, this.apz.serverToken, this.apz.deviceId, params.req.loginRequest.sysDate);
            } catch (e) {
               alert("Login Hashing Failed");
            }
            params.pwdOrig = params.pwd;
            params.req.loginRequest.pwd = hash;
          }

          this.sendReq(req);
      }
   }, getDeviceInfo : function(req) {
      
      ////Init
      this.apz.initNativeService(req);
      var ideSim = false;
      if ( typeof apzIde !== "undefined") {
         ideSim = true;
      }
      var res = {};
      res.id = req.id;
      res.status = true;
      res.status = true;
      res.deviceType = "SIMULATOR";
      res.deviceOs = "SIMULATOR";
      res.deviceId = "SIMULATOR";
      ///Get Screen Size
      if (ideSim) {
         res.screenPpi = apzIde.getScreenPpi();
         res.screenSize = apzIde.getScreenSize();
      } else {
         res.screenPpi = 160;
         res.screenSize = this.apz.getScreenWidth() + "X" + this.apz.getScreenHeight();
      }
	  
      ////Register Screen Size
      this.apz.deviceOs = res.deviceOs;
      this.apz.screenSize = res.screenSize;
      this.apz.screenPpi = res.screenPpi;
      ///Get Device Group
      res.deviceGroup = this.apz.getDeviceGroup();
      ////Populate Lock Rotation
      res.orientation = this.apz.deviceGroupDet.orientation;
      res.lockRotation = true;
      if (res.orientation == "ANY") {
         res.lockRotation = false;
      }
      ///Send Orientation to IDE
      if (ideSim) {
         apzIde.setDeviceGroup(res.deviceGroup);
         res.orientation = apzIde.getOrientation();
      } else {
         res.orientation = "PORTRAIT";
      }
      ///In Case of Appzillon Simulator Signal Show
      if (ideSim) {
         apzIde.showSimulator();
      }
      ////Get Orientation
      ////Call CB ( This will be done from Native layer for Mobile Containers)
      ///Here we directly call the function
      ////CAll BAck..
      Apz.nativeServiceCB(JSON.stringify(res));
   }, getUserPrefs : function(req) {
      ////Init
      this.apz.initNativeService(req);
      var res = {};
      res.id = req.id;
      res.userPrefs = null;
      ///Would have read it from projdef
      Apz.nativeServiceCB(JSON.stringify(res));
   }, getLocation : function(req) {
	this.apz.initNativeService(req);
	var res = {};
	res.id = req.id;
	res.status = true;
	try{
		navigator.geolocation.getCurrentPosition(
		    function(position) {
				res.latitude = position.coords.latitude.toString();
				res.longitude = position.coords.longitude.toString();
				Apz.nativeServiceCB(JSON.stringify(res));
		    },
		    function(error){
				res.latitude = "0";
				res.longitude = "0";
				Apz.nativeServiceCB(JSON.stringify(res));
		    }
		);
	} catch(e){
		this.callNative(res);
	}
   }, encryptData : function(req) {
      ////Init
      this.apz.initNativeService(req);
      var res = {};
      res.id = req.id;
      res.status = true;
      var encryptedStr = req.stringToEncrypt;
      try{
      	encryptedStr = apzIde.encryptData(req.stringToEncrypt);
      } catch (e){
      	res.status = false;
      }
      res.text = encryptedStr;
      ////CAll BAck..
      Apz.nativeServiceCB(JSON.stringify(res));
   }, decryptData : function(req) {
      ////Init
      this.apz.initNativeService(req);
      var res = {};
      res.id = req.id;
      res.status = true;
      var decryptedStr = req.stringToDecrypt;
      try{
      	decryptedStr = apzIde.decryptData(req.stringToDecrypt);
      } catch (e){
      	res.status = false;
      }
      res.text = decryptedStr;
      ////CAll BAck..
      Apz.nativeServiceCB(JSON.stringify(res));
   }, refreshServerNonce : function(params) {
      params.id = "CSNONCE";
      params.internal = true;
      params.ifaceName = "appzillonGetAppSecTokens";
      params.ifaceId = "appzillonGetAppSecTokens";
      if(!params.callBack){
    	  params.callBack = this.refreshServerNonceCB;
    	  params.callBackObj = this;
      }
      this.apz.initNativeService(params);
      var internalErr = false;
      if (!this.apz.isNull(typeof apzIde) && !apzIde.isMockEnabled()) {
         //// Construct Request
         var reqFull = {};
         if(this.apz.isNull(params.id)){
            params.id = "CSNONCE";
         }
         reqFull.appzillonHeader = this.apz.server.getHeader(params);
         var request = {};
         request.appzillonGetAppSecTokensRequest = {};
         request.appzillonGetAppSecTokensRequest.appId = this.apz.appId;
         request.appzillonGetAppSecTokensRequest.deviceId = "SIMULATOR";
         reqFull.appzillonBody = request;
         params.reqFull = reqFull;
         var url = apzIde.getServerUrl();
         var encReqd = apzIde.isEncryptionReqd();
         url = url.replace("https://", "http://");
         if (encReqd == "Y") {
        	var safeKey = Math.random().toString(36).substr(2,8) + Math.random().toString(36).substr(2,8);
            this.publicKey = this.apz.getFile("apps/" + this.apz.appId + "/rsakeys/" + this.apz.appId + "_Public.key");
            reqFull.appzillonSafe = apzIde.encryptWithPublicKey(safeKey, this.apz.encryptionKeyFileName);
            reqFull.appzillonHeader = apzIde.encryptWithKey(JSON.stringify(reqFull.appzillonHeader), safeKey);
            reqFull.appzillonBody = apzIde.encryptWithKey(JSON.stringify(reqFull.appzillonBody), safeKey);
            if(this.apz.isNull(reqFull.appzillonHeader) || this.apz.isNull(reqFull.appzillonBody)){
               internalErr = true;
            }
         }
         if(!internalErr){
            var reqStr = JSON.stringify(reqFull);
            var nsObj = this;
            $.ajax({
               url: url,
               type: 'POST',
               cache: false,
               data: reqStr,
               contentType: 'application/json',
               dataType: 'json',
               async: false,
               success: function(res) {
                  params.status = true;
                  nsObj.counter = 0;
                  if (encReqd == "Y") {
                	 var safeKey = apzIde.decryptWithPublicKey(res.appzillonSafe, nsObj.publicKey);
                     var decrypHeader = apzIde.decryptWithKey(res.appzillonHeader, safeKey);
                     var decrypBody = apzIde.decryptWithKey(res.appzillonBody, safeKey);
                     if (res.appzillonErrors) {
                        var decrypErrors = apzIde.decryptWithKey(res.appzillonErrors, safeKey);
                        if(nsObj.apz.isNull(decrypErrors)){
                           internalErr = true;
                        } else {
                           res.appzillonErrors = JSON.parse(decrypErrors);
                        }
                     }
                     if(nsObj.apz.isNull(decrypHeader) || nsObj.apz.isNull(decrypBody)){
                        internalErr = true;
                     } else {
                        res.appzillonHeader = JSON.parse(decrypHeader);
                        res.appzillonBody = JSON.parse(decrypBody);
                     }
                  }
                  var appTokn = res.appzillonBody.appzillonGetAppSecTokensResponse;
                  if (appTokn) {
                     nsObj.safeToken = res.appzillonBody.appzillonGetAppSecTokensResponse.safeToken;
                     nsObj.sessionToken = res.appzillonBody.appzillonGetAppSecTokensResponse.sessionToken;
                     nsObj.serverNonce = res.appzillonBody.appzillonGetAppSecTokensResponse.serverNonce;
                  }
                  if(internalErr){
                     params.status = false;
                     res.appzillonErrors = [{"errorCode":"APZ-CNT-330"}];
                  }
                  params.resFull = res;
                  Apz.nativeServiceCB(JSON.stringify(params));
               },
               error: function(res) {
                  params.status = false;
                  Apz.nativeServiceCB(JSON.stringify(params));
               }
            });
         } else {
            params.status = false;
            params.resFull = {"appzillonErrors":[{"errorCode":"APZ-CNT-330"}]};
            params.errorCode = "APZ-CNT-330";
            Apz.nativeServiceCB(params);
         }
      } else {
         params.status = true;
         Apz.nativeServiceCB(params);
      }
   }, refreshServerNonceCB : function(params) {
	   if(!params.status){
		   this.apz.dispMsg({message:"Failed to get Server Nonce"});
	   }
   }, hashSHA256 : function(req) {
      ////Init
      this.apz.initNativeService(req);
      var hash = '';
      try {
         hash = apzIde.hashSHA256(req.body.text, req.body.salt);
      } catch (e) {
      }
      var res = {};
      res.id = req.id;
      res.status = true;
      res.text = hash;
      ////Call BAck..
      Apz.nativeServiceCB(JSON.stringify(res));
   }, hashPwd : function() {
      
   }, showSplash : function(req) {
       //this.callNative(req);  
   }, hideSplash : function(req) {
	   //Nothing To Do
   }, startOrientationListener: function(req) {      
      this.apz.initNativeService(req);         
   }, nativeServiceExt : function(req) {
      this.callNative(req);
   }, openCamera : function(req) {
      this.callNative(req);
   }, startBeacon : function(req) {
      this.callNative(req);
   }, stopBeacon : function(req) {
      this.callNative(req);
   }, callNumber : function(req) {
      this.callNative(req);
   }, base64ToFile : function(req) {
      this.callNative(req);
   }, scanFinger : function(req) {
      this.callNative(req);
   }, openUrl : function(req) {
      this.callNative(req);
   }, fileToBase64 : function(req) {
      this.callNative(req);
   }, getFileSize : function(req) {
      this.callNative(req);
   }, launchWebview : function(req) {
      this.callNative(req);
   }, closeWebview : function(req) {
      this.callNative(req);
   }, getInboxSMS : function(req) {
      this.callNative(req);
   }, setRingtone : function(req) {
      this.callNative(req);
   }, startCallListener : function(req) {
      this.callNative(req);
   }, getMissedCalls : function(req) {
      this.callNative(req);
   }, zip : function(req) {
      this.callNative(req);
   }, unzip :function(req) {
      this.callNative(req);
   }, startAccelerometer : function(req) {
      this.callNative(req);
   }, stopAccelerometer : function(req) {
      this.callNative(req);
   }, scanBarcode : function(req) {
      this.callNative(req);
   }, createCalendarEvent : function(req) {
      this.callNative(req);
   }, editCalendarEvent : function(req) {
      this.callNative(req);
   }, deleteCalendarEvent : function(req) {
      this.callNative(req);
   }, startCompass : function(req) {
      this.callNative(req);
   }, stopCompass : function(req) {
      this.callNative(req);
   }, executeSql : function(req) {
      this.callNative(req);
   }, deviceDetails :function(req) {
      this.callNative(req);
   }, startBatteryMonitor : function(req) {
      this.callNative(req);
   }, stopBatteryMonitor : function(req) {
      this.callNative(req);
   }, encryptFile : function(req) {
      this.callNative(req);
   }, decryptFile : function(req) {
      this.callNative(req);
   }, geofencing : function(req) {
      this.callNative(req);
   }, startLocationTracking : function(req) {
      this.callNative(req);
   }, stopLocationTracking : function(req) {
      this.callNative(req);
   }, startGesture : function(req) {
      this.callNative(req);
   }, stopGesture : function(req) {
      this.callNative(req);
   }, fileBrowser : function(req) {
      this.callNative(req);
   }, getFileContent : function(req) {
      this.callNative(req);
   }, createFile : function(req) {
      this.callNative(req);
   }, deleteFile : function(req) {
      this.callNative(req);
   }, openFile : function(req) {
      this.callNative(req);         
   }, sendMail : function(req) {
      this.callNative(req);
   }, addContact : function(req) {
      this.callNative(req);
   }, deleteContact : function(req) {
      this.callNative(req);
   }, searchContact : function(req) {
      this.callNative(req);
   }, editContact : function(req) {
      this.callNative(req);
   }, fetchContact : function(req) {
      this.callNative(req);
   }, drivingDirection : function(req) {
      this.callNative(req);
   }, loadMap : function(req) {
      this.callNative(req);
   }, locationSelector : function(req) {
      this.callNative(req);
   }, currentLocale : function(req) {
      this.callNative(req);
   }, saveReport : function(req) {
      this.callNative(req);
   }, sendReq : function(req) {
      this.apz.initNativeService(req);
      var params = req.params;
      var reqFull = params.reqFull;
      this.counter = this.counter + 1;
      reqFull.appzillonHeader.clientNonce = this.counter;
      reqFull.appzillonHeader.sessionToken = this.sessionToken;
      reqFull.appzillonHeader.serverNonce = this.serverNonce;
      var url = params.url.replace("https://", "http://");
      var encReq = null;
      var internalErr = false;
      var resObj = req;
      resObj.params = {id: params.id, reqId: params.reqId};
      if (this.apz.encryption == "Y") {
         encReq = {};
         encReq.appzillonSafe = apzIde.encryptWithPublicKey(this.safeToken, this.publicKey);
         encReq.appzillonHeader = apzIde.encryptWithKey(JSON.stringify(reqFull.appzillonHeader), this.safeToken);
         encReq.appzillonBody = apzIde.encryptWithKey(JSON.stringify(reqFull.appzillonBody), this.safeToken);
         if(this.apz.isNull(encReq.appzillonSafe) || this.apz.isNull(encReq.appzillonHeader) || this.apz.isNull(encReq.appzillonBody)){
            internalErr = true;
         }
      }
      if (this.apz.dataIntegrity == "Y") {
         var apzQopStr = apzIde.getQualityOfPayload(JSON.stringify(params.reqFull), this.counter, this.serverNonce);
         var apzQOP = {
            "appzillonQop": apzQopStr
         };
         encReq = $.extend({}, apzQOP, (encReq || params.reqFull));
         if(this.apz.isNull(apzQopStr)){
            internalErr = true;
         }
      }
      if(!internalErr){
         var reqStr = encReq ? JSON.stringify(encReq) : JSON.stringify(params.reqFull);
         var nsObj = this;
         $.ajax({
            url: url,
            type: params.method,
            cache: false,
            data: reqStr,
            contentType: 'application/json',
            dataType: 'json',
            async: params.async,
            success: function(res) {
               resObj.params.status = true;
               req.status = true;
               var resFull = null;
               var encStr = "";
               if (nsObj.apz.encryption == "Y") {
                  resFull = {};
                  var decrypSafeToken = apzIde.decryptWithPublicKey(res.appzillonSafe, nsObj.publicKey);
                  var decrypHeader = apzIde.decryptWithKey(res.appzillonHeader, decrypSafeToken);
                  var decrypBody = apzIde.decryptWithKey(res.appzillonBody, decrypSafeToken);
                  var decrErrors = "";
                  if (res.appzillonErrors) {
                     decrErrors = apzIde.decryptWithKey(res.appzillonErrors, decrypSafeToken);
                     if(nsObj.apz.isNull(decrErrors)){
                        internalErr = true;
                     } else {
                        resFull.appzillonErrors = JSON.parse(decrErrors);
                     }
                  }
                  nsObj.safeToken = decrypSafeToken;
                  if(nsObj.apz.isNull(decrypSafeToken) || nsObj.apz.isNull(decrypHeader) || nsObj.apz.isNull(decrypBody)){
                     internalErr = true;
                  } else {
                	 try{
	                     resFull.appzillonHeader = JSON.parse(decrypHeader);
	                     resFull.appzillonBody = JSON.parse(decrypBody);
                	 } catch (e){
                		 resFull.appzillonBody = {};
                	 }
                  }
                  if(!internalErr){
                	  if(!nsObj.apz.isNull(decrErrors)){ 
                	   encStr = '{"appzillonHeader":"'+decrypHeader +'","appzillonBody":"'+ decrypBody+'","appzillonErrors":"'+ decrErrors+'"}';
                	  }else{
                	   encStr = '{"appzillonHeader":'+decrypHeader +',"appzillonBody":'+ decrypBody+'}';  
                	  }
                	}
               }
               if (nsObj.apz.dataIntegrity == "Y") {
                  var sQop = res["appzillonQop"];
                  delete res["appzillonQop"];
                  var cQop = "";
                  if(!nsObj.apz.isNull(encStr)){
                	  cQop = apzIde.getQualityOfPayload(encStr, nsObj.counter, nsObj.serverNonce);
                  } else { 
                	  //cQop = apzIde.getQualityOfPayload(JSON.stringify(res), nsObj.counter, nsObj.serverNonce);
                	  //// TBC - Commenting above and adding below as temp fix suggested for ISO service sorting issue.
                	  cQop = sQop; 
                  }
                  if (cQop != sQop) {
                     internalErr = true;
                  }
               }
               resObj.params.resFull = resFull || res;
               if(internalErr){
                  req.status = false;
                  resObj.params.resFull.appzillonErrors = [{"errorCode":"APZ-CNT-330"}];
               }
               Apz.nativeServiceCB(req);
            },
            error: function(res) {
               resObj.status = true;
               resObj.params.status = false;
               Apz.nativeServiceCB(req);
            }
         });
      } else {
    	 resObj.status = false;
         resObj.params.resFull = {"appzillonErrors":[{"errorCode":"APZ-CNT-330"}]};
         Apz.nativeServiceCB(req);
      }
   }, signaturePad : function(req) {
      this.callNative(req);
   }, facebookLogin : function(req) {
      this.callNative(req);
   }, googleLogin : function(req) {
      this.callNative(req);
   }, linkedinLogin : function(req) {
      this.callNative(req);
   }, twitterLogin : function(req) {
      this.callNative(req);
   }, youtube : function(req) {
      this.callNative(req);
   }, startIdleTimer : function(req) {
      this.callNative(req);
   }, lockRotation : function(req) {
      this.callNative(req);
   }, unlockRotation : function(req) {
      this.callNative(req);
   }, setOrientation : function(req) {
      this.callNative(req);
   }, vibrate : function(req) {
      this.callNative(req);
   }, voice : function(req) {
      this.callNative(req);
   }, detectEvents : function(req) {
      this.callNative(req);
   }, wipeOut : function(req) {
      this.callNative(req);
   }, getIP : function(req) {
      this.callNative(req);
   }, getAppVersion : function(req) {
      this.callNative(req);
   }, closeApplication : function(req) {
      this.callNative(req);
   }, multiviewOpen : function(req) {
      this.callNative(req);
   }, multiviewClose : function(req) {
      this.callNative(req);
   }, captureNotes : function(req) {
      this.callNative(req);
   }, enablePullDown : function(req) {
      this.callNative(req);
   }, disablePullDown : function(req) {
      this.callNative(req);
   }, hideRefresh : function(req) {
      this.callNative(req);
   }, startAugmentation : function(req) {
      this.callNative(req);
   }, reloadAugmentation : function(req) {
      this.callNative(req);
   }, videoRecording : function(req) {
      this.callNative(req);
   }, audio : function(req) {
      this.callNative(req);
   }, getNotification : function(req) {
      this.callNative(req);
   }, deleteNotification : function(req) {
      this.callNative(req);
   }, updateNotification : function(req) {
      this.callNative(req);
   }, launchApp : function(req) {
      this.callNative(req);
   }, subappDelete : function(req) {
      this.callNative(req);
   }, getInstructions : function(req) {
      this.callNative(req);
   }, upgradeRequired : function(req) {
      this.callNative(req);
   }, upgradeApp : function(req) {
      this.callNative(req);
   }, setUserPrefs : function(req) {
      this.callNative(req);      
   }, offlineData : function(req) {
      this.callNative(req);
   }, smsSend : function(req) {
      this.callNative(req);
   }, startSMSListener : function(req) {
      this.callNative(req);
   }, stopSMSListener : function(req) {
      this.callNative(req);
   }, store : function(req) {
      this.callNative(req);
   }, retrieve : function(req) {
      this.callNative(req);
   }, startKeyboardListener : function(req) {
      this.callNative(req);
   }, stopKeyboardListener : function(req) {
      this.callNative(req);
   }, startNotificationListener : function(req) {
      this.callNative(req);
   }, stopNotificationListener : function(req) {
      this.callNative(req);
   }, getPref : function(req) {
      this.callNative(req);
   }, setPref : function(req) {
      this.callNative(req);
   }, callNativeCBwithErrorCode : function(req) {
      this.callNative(req);
   }, biometricAuth : function(req) {
      this.callNative(req);
   }, whatsApp : function(req) {
      this.callNative(req);
   }, getSimInfo : function(req) {
      this.callNative(req);
   }, sendSmsBySID : function(req) {
      this.callNative(req);
   }, documentScanner : function(req) {
      this.callNative(req);
   }, uploadFile : function(req) {
      this.callNative(req);
   }, downloadFile : function(req) {
      this.callNative(req);
   }, sendNFC : function(req) {
      this.callNative(req);
   }, receiveNFC : function(req) {
      this.callNative(req);
   }, stopNFC : function(req) {
      this.callNative(req);
   }, readFile : function(req) {
      this.callNative(req);
   }, printFile : function(req) {
      this.callNative(req);
   }, printScreen : function(req) {
      this.callNative(req);
   }, makeSkypeCall : function(req) {
      this.callNative(req);
   }, deepLinking : function(req) {
      this.callNative(req);
   }
};
getMCABootstrapData = function(){
    var path = apz.getConfigPath() + "/mcaprocess/" + "process.json";
    var params = { "context" : {}};
	params.config={};
	params.config.rootContext="";
	var content = apz.getFile(path);
	if(content){
		params.context.processJson = JSON.parse(content);
	}
	return params;
}
window.onerror = function(msg, url, lineNo, columnNo, error){
   var string = msg.toLowerCase();
    var substring = "script error";
    if (string.indexOf(substring) > -1){
        console.log('Script Error Occured');
    } else {
        var message = [
            'Message: ' + msg,
            'URL: ' + url,
            'Line: ' + lineNo,
            'Column: ' + columnNo,
            'Error object: ' + JSON.stringify(error)
        ].join(' -- ');
        console.log(message);
    }
}