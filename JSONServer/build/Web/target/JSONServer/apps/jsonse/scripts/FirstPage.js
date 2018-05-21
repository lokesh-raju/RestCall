apz.jsonse.json = {};

apz.app.onLoad_FirstPage = function(params){
    alert("OnLoad");
    debugger;
    

var lServerParams = {
        "ifaceName": "Customer",
        "appId": "jsonse",
        "buildReq": "N",
        "req": "",
        "paintResp": "Y",
        "async": "true",
        "callBack": apz.jsonse.json.fnServercallBack,
        "callBackObj": "",
    };
    apz.server.callServer(lServerParams);
   
}

apz.jsonse.json.fnServercallBack = function(params){
    debugger;
    apz.data.loadData("Customer","jsonse");
}
