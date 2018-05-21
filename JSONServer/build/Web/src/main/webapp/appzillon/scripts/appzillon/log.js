Apz.Log = function(apz) {
   ////Core Instance
   this.apz = apz;
   this.logs = "";
   this.lines = [];
   this.levels = [];
   this.levels["F"] = 0;
   this.levels["E"] = 1;
   this.levels["W"] = 2;
   this.levels["I"] = 3;
   this.levels["D"] = 4;
   this.typeDescription = [];
   this.typeDescription["F"] = "Fatal";
   this.typeDescription["E"] = "Error";
   this.typeDescription["W"] = "Warning";
   this.typeDescription["I"] = "Info";
   this.typeDescription["D"] = "Debug";
};
Apz.Log.prototype = {
   log : function(type, msg) {
	   /* Params contains the below values
	       *** type, msg ***
	   */
      try {
         var level = this.levels[type];
         if ((level <= this.levels[this.apz.logLevel]) || (level === 0 )) {
            this.appendLog(msg, type);
         }
      } catch(e) {
      }
   }, debug : function(msg) {
      this.log("D", msg);
   }, info : function(msg) {
      this.log("I", msg);
   }, warning : function(msg) {
      this.log("W", msg);
   }, error : function(msg) {
      this.log("E", msg);
   }, fatal : function(msg) {
      this.log("F", msg);
   }, appendLog : function(msg, type) {
	   /* Params contains the below values
	       *** msg, type ***
	   */
      var level = this.levels[type];
      if (this.lines.length >= this.apz.noOfLogLines) {
         ////Remove first Line
         this.lines.splice(0, 1);
      }
      msg = "[Date : " + this.apz.getCurrTimeStamp() + "  Type : " + this.typeDescription[type] + "  Message : " + msg+"]\n";
      this.lines[this.lines.length] = msg;
      if ((level === 0) || (this.lines.length >= this.apz.noOfLogLines)) {
         ///Send Log Depending on Choice
         if (this.apz.sendLog == "Y") {
            this.sendLog();
         } else if (this.apz.sendLog == "C") {
            var params = {};
            params.code = "APZ-INF-001";
            params.callBackObj = this;
            params.callBack = this.sendLogConfirm;
            this.apz.dispMsg(params);
         }
      }
   }, sendLogConfirm : function(params) {
      if (params.choice) {
         this.sendLog();
      }
   }, sendLog : function() {
      var body = {}
      body.appzillonErrorLoggingRequest = {};
      body.appzillonErrorLoggingRequest.error = this.lines.join("\n");
      ////Call Server
      var params = {};
      params.internal = true;
      params.id = "SENDLOG";
      params.ifaceName = "appzillonErrorLogging";
      params.req = body;
      params.callBackObj = this;
      params.callBack = this.sendLogCB;
      this.apz.server.sendReq(params);
   }, sendLogCB : function(params) {
      ////Handle Failure here..
   }
};
