Apz.Data = function(apz) {
   this.apz = apz;
   //////////////Private Variables//////////////
   ////Model Data
   this.scrdata = {};
};
///////////////////Prototype Definition///////////////////////
Apz.Data.prototype = {
   loadData : function(ifaceId, appId) {
	   /* Params contains the below values
	       *** ifaceId, appId ***
	   */
      if(!this.apz.isNull(ifaceId)){
         ifaceId = appId ? appId+"__"+ifaceId : this.apz.currAppId+"__"+ifaceId;
      }
      var containers = this.apz.scrMetaData.containers;
      var noOfContainers = containers.length;
      if (noOfContainers > 0) {
         var params = {};
         params.dataRecNo = 0;
         params.action = "C";
         for (var c = 0; c < noOfContainers; c++) {
     		var container = containers[c];
     		params.containerId = container.id;
        	if (!this.apz.isNull(ifaceId)) {
        		if(container.ifaces.indexOf(ifaceId) > -1){
        			this.getContainerData(params);
        		}
        	} else {
        		this.getContainerData(params);
        	}
         }
      }
      ///Re adjust height
      //this.apz.readjustHeight();
   }, clearData : function(ifaceId, appId) {
	   /* Params contains the below values
	       *** ifaceId, appId ***
	   */
      var ifaceDet;
      if (!this.apz.isNull(ifaceId)) {
         ifaceId = appId ? appId+"__"+ifaceId : this.apz.currAppId+"__"+ifaceId;
         var ifaceName = this.apz.getIfaceName(ifaceId);
         ifaceDet = this.apz.getIfaceObj(ifaceName, appId);
      }
      ////Clear Data For All Containers..
      var nodes = null;
      var containers = this.apz.scrMetaData.containers;
      var noOfContainers = containers.length;
      if (noOfContainers > 0) {
         for (var c = 0; c < noOfContainers; c++) {
            var container = containers[c];
            var containerId = container.id;
            var containerObj = document.getElementById(containerId);
            if(!this.apz.isNull(containerObj)){
               if (!this.apz.isNull(ifaceId)) {
                  if(container.ifaces.indexOf(ifaceId) > -1){
                     this.clearContainerData(containerId);
                  }
               } else {
                  this.clearContainerData(containerId);
               }
            }
         }
      }
      if (ifaceDet) {
         nodes = ifaceDet.nodes;
      } else {
         nodes = this.apz.scrMetaData.nodes;
      }
      var noOfNodes = nodes.length;
      for (var n = 0; n < noOfNodes; n++) {
         proceed = false;
         if (this.apz.isNull(ifaceId)) {
            proceed = true;
         } else {
            if (!this.apz.isNull(nodes[n].ifaceId)) {
               if (nodes[n].ifaceId == ifaceId) {
                  proceed = true;
               }
            }
         }
         if (proceed) {
            if (nodes[n].multiRec == "Y") {
               nodes[n].currRec = -1;
            } else {
               nodes[n].currRec = 0;
            }
         }
      }
   }, clearContainerData : function(containerId){
	   /* Params contains the below value
	       *** containerId ***
	   */
      var containerData = this.apz.scrMetaData.containersMap[containerId];
      if (containerData.multiRec == "Y") {
         this.clearMRMV(containerData.id);
      } else {
         noOfElms = containerData.elms.length;
         for (var e = 0; e < noOfElms; e++) {
            var elmData = containerData.elms[e];
            var elmId = elmData.id;
            var nodeId = elmData.nodeId;
            var elmName = elmData.name;
            var elmObj = document.getElementById(elmId);
            if ((!this.apz.isNull(nodeId)) && (!this.apz.isNull(elmName)) && (!this.apz.isNull(elmObj))) {
               this.apz.setObjValue(elmObj, null);
            }
         }
      }
      containerData.pageRows = 0;
      containerData.totalRecs = 0;
      containerData.currRec = -1;
      containerData.currPage = 0;
      containerData.totalPages = 0;
   }, getDataPointer : function(nodeId, pRec) {
      var pointer = this.scrdata;
      if (!this.apz.isNull(nodeId)) {
         if (this.apz.isNull(this.apz.scrMetaData.nodesMap[nodeId])) {
            pointer = null;
         } else {
            var parents = this.apz.scrMetaData.nodesMap[nodeId].parents;
            var parentsLen = parents.length;
            var lNodeId = "";
            var recNo = 0;
            //    var lmrParent = this.apz.scrMetaData.nodesMap[nodeId].mrParent;
            for (var p = 0; p <= parentsLen; p++) {
               lNodeId = parents[p];
               if (p == parentsLen) {
                  lNodeId = nodeId;
                  recNo = pRec;
               } else {
                  lNodeId = parents[p];
                  recNo = this.apz.scrMetaData.nodesMap[lNodeId].currRec;
				 
                  if (this.apz.scrMetaData.nodesMap[lNodeId].relType == "1:N") {
                     if (this.apz.scrMetaData.nodesMap[nodeId].mrParent == lNodeId) {
						 if (recNo == -1 || recNo !== pRec) {
							  recNo = pRec;
						  }						 
						 
                     } else {
                        if (recNo == -1) {
                           recNo = 0;
                        }
                     }
                  }
               }
               ////IDERES
               var nodeName = this.apz.getNodeName(lNodeId);
               if (this.apz.scrMetaData.nodesMap[lNodeId].relType == "1:N") {
                  if (!pointer[nodeName]) {
                     pointer = null;
                     break;
                  } else {
                     if (!pointer[nodeName][recNo]) {
                        pointer = null;
                        break;
                     }
                  }
                  pointer = pointer[nodeName][recNo];
               } else {
                  if (!pointer[nodeName]) {
                     pointer = null;
                     break;
                  }
                  pointer = pointer[nodeName];
               }
            }
         }
      }
      return pointer;
   }, getParentDataPointer : function(nodeId, pRec) {  
      var pointer = this.scrdata;
      if (!this.apz.isNull(nodeId)) {
         if (this.apz.isNull(this.apz.scrMetaData.nodesMap[nodeId])) {
            pointer = null;
         } else {
            var parents = this.apz.scrMetaData.nodesMap[nodeId].parents;
            var parentsLen = parents.length;
            var lNodeId = "";
            var recNo = 0;
            for (var p = 0; p < parentsLen; p++) {
               lNodeId = parents[p];
               if (p == parentsLen - 1) {
                  recNo = this.apz.scrMetaData.nodesMap[lNodeId].currRec;
               } else {
                  lNodeId = parents[p];
                  recNo = this.apz.scrMetaData.nodesMap[lNodeId].currRec;
                  if (this.apz.scrMetaData.nodesMap[lNodeId].relType == "1:N") {
                     if (this.apz.scrMetaData.nodesMap[nodeId].mrParent == lNodeId) {
                        if (recNo == -1) {
                           recNo = pRec;
                        }
                     } else {
                        if (recNo == -1) {
                           recNo = 0;
                        }
                     }
                  }
               }
               var nodeName = this.apz.getNodeName(lNodeId);
               if (this.apz.scrMetaData.nodesMap[lNodeId].relType == "1:N") {
                  if (!pointer[nodeName]) {
                     pointer = null;
                     break;
                  } else {
                     if (!pointer[nodeName][recNo]) {
                        pointer = null;
                        break;
                     }
                  }
                  pointer = pointer[nodeName][recNo];
               } else {
                  if (!pointer[nodeName]) {
                     pointer = null;
                     break;
                  }
                  pointer = pointer[nodeName];
               }
            }
         }
      }
      return pointer;
   }, getNoOfRecs : function(nodeId) {
      var noOfRecs = 0;
      ////IDERES
      var nodeName = this.apz.getNodeName(nodeId);
      var lparent = this.apz.scrMetaData.nodesMap[nodeId].parent;
      var parentCurrRec = -1;
      ////Get Parent's Current Record...
      if (!this.apz.isNull(lparent)) {
         if (this.apz.scrMetaData.nodesMap[lparent]) {
            parentCurrRec = this.apz.scrMetaData.nodesMap[lparent].currRec;
         } else {
            parentCurrRec = 0;
         }
      } else {
         parentCurrRec = -1;
      }
      var lpointer = this.getDataPointer(lparent, parentCurrRec);
      if (lpointer === null) {
         noOfRecs = 0;
      } else {
         if (!lpointer[nodeName]) {
            noOfRecs = 0;
         } else {
            if (this.apz.scrMetaData.nodesMap[nodeId].relType == "1:N") {
               noOfRecs = lpointer[nodeName].length;
            } else {
               noOfRecs = 1;
            }
         }
      }
      return noOfRecs;
   }, getContainerData : function(params) {
	   //Expects containerId, dataRecNo, action
      var myObj = this;
      var containerId = params.containerId;
      var containerObj = document.getElementById(containerId);
      var containerType = this.apz.scrMetaData.containersMap[containerId].type;
      if (containerType == "GAUGE") {
         requirejs([this.apz.getInfraPath() + "/appzillon/gauges.js"], function() {
            var gaugeObj = myObj.apz.scrMetaData.gaugesMap[containerId];
            try {
               var gauges = new Apz.Gauges(myObj.apz);
               gauges.paintGauge(gaugeObj);
            } catch(e) {
               console.log("Problems with Gauges Library");
            }
         });
      } else if (containerType == "CHART") {
         requirejs([this.apz.getInfraPath() + "/appzillon/charts.js"], function() {
            var chartObj = myObj.apz.scrMetaData.chartsMap[containerId];
            try {
               var charts = new Apz.Charts(myObj.apz);
               charts.paintChart(chartObj);
            } catch(e) {
               console.log("Problems with Charts Library");
            }
         });
      } else if(!this.apz.isNull(containerObj)){
         var containerData = this.apz.scrMetaData.containersMap[containerId];
         // var ltype = containerData.type;
         var containerMultiRec = containerData.multiRec;
         var lnodes = containerData.nodes;
         var noOfNodes = lnodes.length;
         var noOfElms = 0;
         var mrParent = "";
         var elmData;
         var nodeId = "";
         var elm = "";
         var val = "";
         var rowNo = -1;
         var getAllRecords = false;
         var totalRecs = 0;
         var currRecDataPointers = [];
         var recDataPointers = [];
         var dataPointer;
         var paginationStyle = this.apz.scrMetaData.containersMap[containerId].paginationStyle;
         if (noOfNodes > 0) {
            ////Set Current Records and Populate Data Pointers for all Nodes for the Record Sent..
            for (var n = 0; n < noOfNodes; n++) {
               nodeId = lnodes[n];
			   if((containerType == 'FORM' || containerType == 'NAVBAR' ||  containerType == 'LIST') && this.apz.scrMetaData.nodesMap[nodeId].relType == "1:1"){
				 var mrParent = this.apz.scrMetaData.nodesMap[nodeId].mrParent;
				 params.dataRecNo =  this.apz.scrMetaData.nodesMap[mrParent].currRec;
			   }
               dataPointer = this.getDataPointer(nodeId, params.dataRecNo);
               if (dataPointer !== null) {
                  currRecDataPointers[nodeId] = dataPointer;
                  this.apz.scrMetaData.nodesMap[nodeId].currRec = params.dataRecNo;
               } else {
                  if (this.apz.scrMetaData.nodesMap[nodeId].relType == "1:N") {
                     this.apz.scrMetaData.nodesMap[nodeId].currRec = -1;
                  } else {
                     this.apz.scrMetaData.nodesMap[nodeId].currRec = 0;
                  }
                  currRecDataPointers[nodeId] = null;
               }
            }
            ////Get Total Records..
            nodeId = lnodes[0];
            mrParent = this.apz.scrMetaData.nodesMap[nodeId].mrParent;
            totalRecs = this.getNoOfRecs(mrParent);
            noOfElms = containerData.elms.length;
            if (containerMultiRec == "Y") {
				//Close Responsive Table Rows
	            var tableObj = $("#"+containerId+"_table");
	            if(tableObj.hasClass("dataTable")){
	            	this.apz.closeResponsiveTableRows(tableObj);
	            }
               ////Clear All records..
               if (totalRecs == 0) {
                  this.apz.scrMetaData.containersMap[containerId].currPage = 0;
                  this.apz.scrMetaData.containersMap[containerId].totalPages = 0;
                  this.apz.scrMetaData.containersMap[containerId].currRec = -1;
                  this.apz.scrMetaData.containersMap[containerId].totalRecs = 0;
                  this.clearMRMV(containerId);
               } else {
                  var newPage = this.getPage(containerId, params.dataRecNo);
                  var rowNo = this.getRow(containerId, params.dataRecNo);
                  var currPage = this.apz.scrMetaData.containersMap[containerId].currPage;
                  var pageChange = false;
                  var pageRows = this.apz.scrMetaData.containersMap[containerId].pageRows;
                  var pageSize = this.apz.scrMetaData.containersMap[containerId].pageSize;
                  var totalPages = this.getPage(containerId, totalRecs - 1);
                  var startRec = -1;
                  var endRec = -1;
                  if (paginationStyle == "APPEND") {
                     if (params.action == "N") {
                        newPage = currPage;
                     }
                  }
                  if (newPage != currPage) {
                     pageChange = true;
                  }
                  if ((params.action == "C") || (params.action == "D") || ((pageChange) && (this.apz.scrMetaData.containersMap[containerId].paginationStyle != "APPEND"))) {
                     this.clearMRMV(containerId);
                  }
                  ////Set Page Counters for Container...
                  this.apz.scrMetaData.containersMap[containerId].currPage = newPage;
                  this.apz.scrMetaData.containersMap[containerId].totalPages = totalPages;
                  this.apz.scrMetaData.containersMap[containerId].currRec = params.dataRecNo;
                  this.apz.scrMetaData.containersMap[containerId].totalRecs = totalRecs;
                  if (pageChange) {
                     ////Page Changeed..
                     var startRow = 0;
                     if (this.apz.scrMetaData.containersMap[containerId].paginationStyle == "APPEND") {
                        startRow = params.dataRecNo
                     }
                     startRec = this.getDataRec(containerId, startRow);
                  } else {
                     ////Same Page.. Start Record Depends on the Action.
                     /*
                     * Action is B (Insert Before) ---> Painting Should start from the Newly insserted Record.. i.e  pdatarecno
                     * Action is A (Insert After)  ---> Painting Should start from the Newly insserted Record.. i.e  pdatarecno
                     * Action is L (Insert After)  ---> Painting Should start from pageRows..
                     * Action is C (Repopulate )      ---> Painting Should Start from Record 0;
                     * Action is D (Deletion )     ---> Painting Should Start from first Rercord of the Current Page;
                     * Action is N (Navigation )      ---> Painting Should Start from Record pdatarecno;
                     * Action is P (Navigation )      ---> Page styling Append;
                     */
                     if (params.action == "B") {
                        startRec = params.dataRecNo;
                     } else if (params.action == "A") {
                        startRec = params.dataRecNo;
                     } else if (params.action == "L") {
                        startRec = params.dataRecNo;
                     } else if (params.action == "C") {
                        startRec = 0;
                     } else if (params.action == "D") {
                        startRec = this.getDataRec(params.containerId, 0);
                     } else if (params.action == "N") {
                        startRec = params.dataRecNo;
                     } else if (params.action == "P") {
                        startRec = params.dataRecNo;
                     }
                  }
                  ////Decide End Record.. Minimum of page Size and Available Records..
                  //var endRec = -1;
                  if (paginationStyle == "APPEND") {
                     endRec = (newPage * pageSize) - 1;
                     if (endRec >= totalRecs) {
                        endRec = totalRecs - 1;
                     }
                  } else if (paginationStyle == "PAGE1" || paginationStyle == "PAGE2" || paginationStyle == "PAGE3") {
                     endRec = this.getDataRec(containerId, (pageSize - 1));
                     if (endRec >= totalRecs) {
                        endRec = totalRecs - 1;
                     }
                  } else {
                     endRec = totalRecs - 1;
                  }
                  /////Paint MRMV
                  if ((startRec > -1) && (startRec <= endRec)) {
                     ////Painting Required..
                     for (var r = startRec; r <= endRec; r++) {
                        rowNo = this.getRow(containerId, r);
                        ////Add a Row..
                        this.addRow(containerId, rowNo);
                        ////Get Data Pointers For Container Nodes..
                        if (r == params.dataRecNo) {
                           recDataPointers = currRecDataPointers;
                        } else {
                           for (var n = 0; n < noOfNodes; n++) {
                              nodeId = lnodes[n];
                              dataPointer = this.getDataPointer(nodeId, r);
                              if (dataPointer !== null) {
                                 recDataPointers[nodeId] = dataPointer;
                              } ////APZFIX
							  else {
								  recDataPointers[nodeId] = {}; 
							  }
                           }
                        }
                        ////Paint Elements..
                        for (var e = 0; e < noOfElms; e++) {
                           elmData = containerData.elms[e];
                           nodeId = elmData.nodeId;
                           elm = elmData.name;
                           if ((!this.apz.isNull(nodeId)) && (!this.apz.isNull(elm))) {
                              if (this.apz.containsKey(recDataPointers, nodeId)) {
                                 //Data Model Field..//// APZFIX
                                if(apz.isNull(recDataPointers[nodeId])){
									val="";
								}else{
                                 val = recDataPointers[nodeId][elm];
								}
							
                                 var params = {};
                                 params.elmData = elmData;
                                 params.recNo = rowNo;
                                 params.value = val;
                                 this.apz.setValue(params);
                              }
                           }
                           //}
                        }
                     }
                     ///Clear Additional Rows..
                     if (this.apz.scrMetaData.containersMap[containerId].pageRows > endRec+1) {
                        var removeTill = this.apz.scrMetaData.containersMap[containerId].pageRows;
                        if (paginationStyle == "APPEND") {
                           removeTill = removeTill + endRec;
                        }
                        for (var dr = endRec + 1; dr <= removeTill; dr++) {
                           this.clearRow(containerId, dr);
                           this.deleteRow(containerId, dr);
                        }
                     }
                  }
                     this.apz.setTableHeight(containerId,false);
               }
            } else {
               this.apz.scrMetaData.containersMap[containerId].currRec = params.dataRecNo;
               this.apz.scrMetaData.containersMap[containerId].totalRecs = totalRecs;
               for (var e = 0; e < noOfElms; e++) {
                  elmData = containerData.elms[e];
                  nodeId = elmData.nodeId;
                  elm = elmData.name;
                  var params = {};
                  params.elmData = elmData;
                  params.recNo = -1;
                  if ((!this.apz.isNull(nodeId)) && (!this.apz.isNull(elm))) {
                     if (this.apz.containsKey(currRecDataPointers, nodeId)) {
                        if (this.apz.isNull(currRecDataPointers[nodeId])) {
                           val = "";
                        } else {
                           val = currRecDataPointers[nodeId][elm];
                        }
                        ///Data Model Field..
                        params.value = val;
                        this.apz.setValue(params);
                     } else {
                        params.value = null;
                        this.apz.setValue(params);
                     }
                  }
               }
            }
            ////Set Page Coubters..
            this.setContainerCounters(containerId);
         }
      }
   }, getData : function(params) {
	   //Expects containerId, dataRecNo, action, tracker
      var group = this.apz.scrMetaData.containersMap[params.containerId].group;
      var groupData;
      var noOfContainers = 0;
      var container = "";
      var nodes;
      ////Get Containers..
      if (this.apz.isNull(group)) {
         ////Group is Null..
         params.tracker[params.containerId] = "Y";
         this.getContainerData(params);
         nodes = this.apz.scrMetaData.containersMap[params.containerId].nodes;
      } else {
         ////Group Exists..
         groupData = this.apz.scrMetaData.groupsMap[group];
         lnoofcontainers = groupData.containers.length;
         if (lnoofcontainers > 0) {
            for (var c = 0; c < lnoofcontainers; c++) {
               container = groupData.containers[c];
               params.tracker[container] = "Y";
               params.containerId = container;
               this.getContainerData(params);
            }
         }
         nodes = groupData.nodes;
      }
      ////Child Processing...
      var noOfNodes = nodes.length;
      var nodeId = "";
      var childNode;
      var noOfChilds = 0;
      if (noOfNodes > 0) {
         for (var n = 0; n < noOfNodes; n++) {
            nodeId = nodes[n];
            noOfChilds = this.apz.scrMetaData.nodesMap[nodeId].childs.length;
            if (noOfChilds > 0) {
               for (var c = 0; c < noOfChilds; c++) {
                  childNode = this.apz.scrMetaData.nodesMap[nodeId].childs[c];
                  var group = this.apz.scrMetaData.nodesMap[childNode].group;
                  if (!this.apz.isNull(group)){
                     var containers = this.apz.scrMetaData.groupsMap[group].containers;
                  	 if (containers.length > 0) {
                     	container = containers[0];
                     	if (!this.apz.containsKey(params.tracker, container)) {
                           var args = {};
                           args.containerId = container;
                           args.dataRecNo = 0;
                           args.action = "C";
                           args.tracker = params.tracker;
                        	this.getData(args);
                     	}
                     }
                  }
               }
            }
         }
      }
   }, buildData : function(ifaceId, appId) {
	   /* Params contains the below values
	       *** ifaceId, appId ***
	   */
      ////Build Data For All Containers..
      if(!this.apz.isNull(ifaceId)){
          ifaceId = appId ? appId+"__"+ifaceId : this.apz.currAppId+"__"+ifaceId;
      }
      var noOfContainers = this.apz.scrMetaData.containers.length;
      var container;
      var dataPointers = [];
      if (noOfContainers > 0) {
         var params = {};
         params.action = "C";
         params.dataPointers = dataPointers;
         for (var c = 0; c < noOfContainers; c++) {
            container = this.apz.scrMetaData.containers[c].id;
            params.containerId = container;
            this.setContainerData(params);
         }
      }
      ////Extract for Specified interface
      if (!this.apz.isNull(ifaceId)) {
         var ifaceName = this.apz.getIfaceName(ifaceId);
		 var ifaceType = this.apz.getIfaceType(ifaceId)
         var ifaceData = {};
		 var len = ifaceId.length;
		 var end4=ifaceId.substr(len - 4);
	     if(ifaceType == "DATABASE"){
			 ifaceData[ifaceName+"_Req"] = this.scrdata[ifaceName+"_Req"];
		 } else if ((end4 == "_Req") || (end4 == "_Res")) {
			  ifaceData[ifaceId] = this.scrdata[ifaceId];
		 } else {
			 ifaceData[ifaceId+"_Req"] = this.scrdata[ifaceId+"_Req"];
			 ifaceData[ifaceId+"_Res"] = this.scrdata[ifaceId+"_Res"];
		 }
         ifaceData = this.apz.copyJSONObject(ifaceData);
         this.apz.scrinterfaceData = ifaceData;
         return ifaceData;
      }
   }, createDataPointer : function(params) {
	   //Expects nodeId, dataRecNo, force
      var pointer = this.scrdata;
      var newObj;
      var mrParent = this.apz.scrMetaData.nodesMap[params.nodeId].mrParent;
      if ((params.nodeId != null) && (params.nodeId != "") && (params.nodeId != "undefined") && (params.nodeId != undefined)) {
         var parents = this.apz.scrMetaData.nodesMap[params.nodeId].parents;
         var parentsLen = parents.length;
         var lNodeId = "";
         var recNo = 0;
         for (var p = 0; p <= parentsLen; p++) {
            if (p == parentsLen) {
               lNodeId = params.nodeId;
               recNo = params.dataRecNo;
            } else {
               lNodeId = parents[p];
               recNo = this.apz.scrMetaData.nodesMap[lNodeId].currRec;
               if (this.apz.scrMetaData.nodesMap[lNodeId].relType == "1:N") {
                  if (this.apz.scrMetaData.nodesMap[params.nodeId].mrParent == lNodeId) {
                     if (recNo == -1) {
                        recNo = params.dataRecNo;
                     }
                  }
               }
            }
            ////IDERES
            var nodeName = this.apz.getNodeName(lNodeId);
            if (recNo >= 0) {
               if (this.apz.scrMetaData.nodesMap[lNodeId].relType == "1:N") {
                  if (!pointer[nodeName]) {
                     pointer[nodeName] = new Array();
                     recNo = 0;
                     pointer[nodeName][0] = {};
                  } else {
                     if (!pointer[nodeName][recNo]) {
                        pointer[nodeName][recNo] = {};
                     } else {
                        ////Record Exists.. If Insert then Create a new Record anyway...
                        if (params.force) {
                           pointer[nodeName].splice(recNo, 0, {});
                        }
                     }
                  }
                  pointer = pointer[nodeName][recNo];
               } else {
                  if (!pointer[nodeName]) {
                     pointer[nodeName] = {};
                  }
                  pointer = pointer[nodeName];
               }
            } else {
               pointer = null;
               break;
            }
         }
      }
      return pointer;
   }, setContainerData : function(params) {
	   /* Params contains the below attributes
	       *** containerId, action, dataPointers ***
	   */
      var containerData = this.apz.scrMetaData.containersMap[params.containerId];
      var type = containerData.type;
      if ((type != "CHART") && (type != "GAUGE") && (type != "CHART")) {
         var readOnly = containerData.readOnly;
         if (this.apz.isNull(readOnly)) {
            readOnly = "N";
         }
         if (readOnly == "N") {
            var multiRec = containerData.multiRec;
            var nodes = containerData.nodes;
            var noOfNodes = nodes.length;
            var noOfElms = 0;
            var elmData;
            var nodeId = "";
            var nodeIdExtName = "";
            var elm = "";
            var elmExName = "";
            var val = "";
            var recNo = -1;
            var rowNo = -1;
            var mrParent = "";
            var dataExists = false;
            if (noOfNodes > 0) {
               ////Get Data Pointers
               for (var n = 0; n < noOfNodes; n++) {
                  nodeId = nodes[n];
                  var nodeName = this.apz.getNodeName(nodeId);
				  if(type != 'FORM' && this.apz.scrMetaData.nodesMap[nodeId].relType == "1:1"){
						var mrParent = this.apz.scrMetaData.nodesMap[nodeId].mrParent;
						recNo =  this.apz.scrMetaData.nodesMap[mrParent].currRec;
				   }else{
						recNo = this.apz.scrMetaData.nodesMap[nodeId].currRec;
				   }                 
                  if (!this.apz.containsKey(params.dataPointers, nodeId)) {
                     if (recNo > -1) {//Data Should Exist...
                        var args = {};
                        args.nodeId = nodeId;
                        args.dataRecNo = recNo;
                        args.force = false;
                        params.dataPointers[nodeId] = this.createDataPointer(args);
                        dataExists = true;
                     }
                  } else {
                     dataExists = true;
                  }
               }
               if (dataExists) {////Data Exists For Atleast one Node
                  if (this.apz.scrMetaData.containersMap[params.containerId].multiRec == "N") {
                     rowNo = -1;
                  } else {
                     rowNo = this.getRow(params.containerId, recNo);
                  }
                  noOfElms = containerData.elms.length;
                  for (var e = 0; e < noOfElms; e++) {
                     elmData = containerData.elms[e];
                     nodeId = elmData.nodeId;
                     nodeIdExtName = elmData.nodeId;
                     elm = elmData.name;
                     elmExtName = elmData.name;
                     var lnodename = this.apz.getNodeName(nodeId);
                     if (elmData.type != "GAUGE") {
                        if ((!this.apz.isNull(nodeId)) && (!this.apz.isNull(nodeIdExtName)) && (!this.apz.isNull(elm)) && (!this.apz.isNull(elmExtName))) {
                           if (this.apz.scrMetaData.nodesMap[nodeId].currRec > -1) {
                              if (!this.apz.isNull(params.dataPointers[nodeId])) {
                                 val = this.apz.getValue(elmData, rowNo);
                                 params.dataPointers[nodeId][elm] = val;
                              }
                           }
                        }
                     }
                  }
               }
            } else {
               //Pure UI Field Conatiner..
            }
         }
      }
   }, setData : function(params) {
	   //Expects containerId, action, dataPointers, tracker
      var group = this.apz.scrMetaData.containersMap[params.containerId].group;
      var groupData;
      var noOfContainers = 0;
      var container = "";
      var nodes;
      ////Set Containers..
      if (this.apz.isNull(group)) {
         ////Group is Null..
         params.tracker[params.containerId] = "Y";
         this.setContainerData(params);
         nodes = this.apz.scrMetaData.containersMap[params.containerId].nodes;
      } else {
         ////Group Exists..
         groupData = this.apz.scrMetaData.groupsMap[group];
         noOfContainers = groupData.containers.length;
         if (noOfContainers > 0) {
            for (var c = 0; c < noOfContainers; c++) {
               container = groupData.containers[c];
               params.tracker[container] = "Y";
               params.containerId = container;
               this.setContainerData(params);
            }
         }
         nodes = groupData.nodes;
      }
      ////Child Processing...
      var noOfNodes = nodes.length;
      var nodeId = "";
      var childNode;
      var noOfChilds = 0;
      if (noOfNodes > 0) {
         for (var n = 0; n < noOfNodes; n++) {
            nodeId = nodes[n];
            noOfChilds = this.apz.scrMetaData.nodesMap[nodeId].childs.length;
            if (noOfChilds > 0) {
               for (var c = 0; c < noOfChilds; c++) {
                  childNode = this.apz.scrMetaData.nodesMap[nodeId].childs[c];
                  var group = this.apz.scrMetaData.nodesMap[childNode].group;
                  if (!this.apz.isNull(group)) {
                  	var containers = this.apz.scrMetaData.groupsMap[group].containers;
                  	if (containers.length > 0) {
                     	container = containers[0];
                     	if (!this.apz.containsKey(params.tracker, container)) {
                     		params.containerId = container;
                        	this.setData(params)
                     	}
                    }
                  }
               }
            }
         }
      }
   }, goToRecord : function(params) {
	  //Expects containerId, dataRecNo, action
      var containerTracker = [];
      var dataPointers = [];
      var id = params.containerId;
      ////Call Set data
      if ((params.action != "D") && (params.action != "B")) {
         params.dataPointers = dataPointers;
         params.tracker = containerTracker;
         this.setData(params);
      }
      //// Call Get Data
      params.dataPointers = [];
      params.tracker = [];
      params.containerId = id;
      this.getData(params);
   }, getRow : function(containerId, dataRec) {
      var tableRow = dataRec;
      if (this.apz.scrMetaData.containersMap[containerId].paginationStyle !== "APPEND") {
         tableRow = dataRec % this.apz.scrMetaData.containersMap[containerId].pageSize;
      }
      return tableRow;
   }, getDataRec : function(containerId, rowNo) {
      var dataRec = -1;
      if (this.apz.scrMetaData.containersMap[containerId].currPage == 0) {
         dataRec = 0;
      } else {
         if (this.apz.scrMetaData.containersMap[containerId].paginationStyle !== "APPEND") {
            dataRec = ((this.apz.scrMetaData.containersMap[containerId].currPage - 1) * (this.apz.scrMetaData.containersMap[containerId].pageSize)) + rowNo;
         } else {
            dataRec = rowNo;
         }
      }
      return dataRec;
   }, getPage : function(containerId, dataRec) {
      var recs = dataRec + 1;
      var pageNo = 1;
      if (dataRec > 0) {
         var mod = recs % this.apz.scrMetaData.containersMap[containerId].pageSize;
         pageNo = Math.floor(recs / this.apz.scrMetaData.containersMap[containerId].pageSize);
         if (mod > 0) {
            pageNo = pageNo + 1;
         }
      }
      return pageNo;
   }, clearRow : function(containerId, rowNo) {
      var id = "";
      var elmData;
      if (this.apz.scrMetaData.containersMap[containerId].multiRec == "N") {
         rowNo = -1;
      }
      ////Clear Data in Rows As well....
      var noElms = this.apz.scrMetaData.containersMap[containerId].elms.length;
      if (noElms > 0) {
         for (var e = 0; e < noElms; e++) {
            elmData = this.apz.scrMetaData.containersMap[containerId].elms[e];
            if(elmData.ui != "Y"){
               var params = {};
               params.elmData = elmData;
               params.recNo = rowNo;
               params.value = null;
            	this.apz.setValue(params);
            }
         }
      }
      this.unselectRow(containerId, rowNo);
   }, unselectRow : function(containerId, rowNo) {
	   /* Params contains the below values
	       *** containerId, rowNo ***
	   */
      rowNo = this.getRow(containerId, rowNo);
      var id = containerId + "_selcb_" + rowNo;
      var obj = document.getElementById(id);
      if (!this.apz.isNull(obj)) {
         $('#' + id)[0].checked = false;
      } else {
         id = containerId + '_row_' + rowNo;
         try {
            className = $('#' + id)[0].className;
            if (className.indexOf('selected') != -1) {
               $('#' + id).removeClass('selected');
            }
         } catch (err) {
         }
      }
   }, clearMRMV : function(containerId) {
      var id = "";
      var tableId = "";
      var obj = document.getElementById(containerId + '_0');
      if (!this.apz.isNull(obj) && $('#' + containerId + '_0')[0].checked) {
         tableId = containerId + "_table";
         this.apz.unSelectAllRows(tableId);
      }
      for (var r = 0; r <= this.apz.scrMetaData.containersMap[containerId].pageRows; r++) {
         id = containerId + "_row_" + r;
         $("#" + id).addClass("ssp");
         $("#"+id).removeClass("fadeIn zoomIn fadeInDown fadeInUp animated");
         if ($("#" + id).hasClass("mbsc-lv-item")) {
            $("#" + id).addClass("sno");
         }
         this.clearRow(containerId, r);
      }
      this.apz.scrMetaData.containersMap[containerId].pageRows = 0;
	  this.apz.scrMetaData.containersMap[containerId].currRec = -1;
	  this.apz.scrMetaData.containersMap[containerId].currPage = 0;
      this.apz.scrMetaData.containersMap[containerId].totalPages = 0;
	  this.apz.scrMetaData.containersMap[containerId].totalRecs = 0;
      ////Move Childs to Top Most Row..
      this.appendnestList(containerId, 0);
   }, isRowSelected : function(containerId, rowNo) {
	   /* Params contains the below values
	       *** containerId, rowNo ***
	       * Response contains below value
	       *** selected(boolean) ***
	   */
      rowNo = this.getRow(containerId, rowNo);
      var selected = false;
      var id;
      var obj;
      var className;
      id = containerId + "_selcb_" + rowNo;
      obj = document.getElementById(id);
      if (!this.apz.isNull(obj)) {
         selected = obj.checked;
      } else {
         id = containerId + '_row_' + rowNo;
         className = $('#' + id)[0].className;
         if (className.indexOf('selected') != -1) {
            selected = true;
         }
      }
      return selected;
   }, createRow : function(containerId) {
	   /* Params contains the below value
	       *** containerId ***
	   */
      var createRow = true;
      if(this.apz.isFunction(this.apz.app.preCreateRow)){
         createRow = this.apz.app.preCreateRow(containerId);
         if (this.apz.isNull(createRow)) {
            createRow = true;
         }
      }
      if (createRow) {
         var dataRecNo = -1;
         var selRow = -1;
         var action = "L";
         var newRec = -1;
         var currRec = -1;
         var multiRec = this.apz.scrMetaData.containersMap[containerId].multiRec;
         /*   ////////////ACTIONS/////////////////
         *           Add Before              B
         *           Add After               A
         *           Add at the End          L
         *           Repopulate              C
         *           Delete                  D
         *           Navigate                N
         *           Page Change             P //Required Only For Pagination Style APPEND
         */ //////////////////////////////////////
         ////BUG-20207 //Temporary fix, Edited data will be lost in case of editable tables
         if(this.apz.scrMetaData.containersMap[containerId].searchIndex !== undefined && this.apz.scrMetaData.containersMap[containerId].searchIndex !== null){
            for(var j=0,IfaceLen=this.apz.scrMetaData.containersMap[containerId].ifaces.length; j<IfaceLen;j++){
               var ifaceName = this.apz.scrMetaData.containersMap[containerId].ifaces[j].indexOf("__");
               ifaceName = this.apz.scrMetaData.containersMap[containerId].ifaces[j].substr(ifaceName+2);
               this.loadData(ifaceName);
            }
            document.getElementById(containerId+'_input_search').value = "";
            this.apz.scrMetaData.containersMap[containerId].searchIndex = null;
         }
         if (multiRec == "Y") {
            //Get First Slected Row..
            for (var r = 0; r < this.apz.scrMetaData.containersMap[containerId].pageRows; r++) {
               if (this.isRowSelected(containerId, r)) {
                  selRow = r;
                  break;
               }
            }
            if (selRow >= 0) {
               newRec = selRow;
               action = "B";
            } else {
               newRec = this.apz.scrMetaData.containersMap[containerId].totalRecs;
               action = "L";
            }
         } else {
            currRec = this.apz.scrMetaData.containersMap[containerId].currRec;
            if (currRec == (this.apz.scrMetaData.containersMap[containerId].totalRecs - 1)) {
               newRec = this.apz.scrMetaData.containersMap[containerId].totalRecs;
               action = "L";
            } else {
               newRec = this.apz.scrMetaData.containersMap[containerId].currRec + 1;
               action = "A";
            }
         }
         if (newRec >= 0) {
            var params = {};
            params.containerId = containerId;
            params.dataRecNo = newRec;
            params.action = action;
            this.createContainerRecord(params);
            ////Go To the New Record...
            this.goToRecord(params);
         }
      }
      if(this.apz.isFunction(this.apz.app.postCreateRow)){
         this.apz.app.postCreateRow(containerId);
      }
         this.apz.setTableHeight(containerId,false);
   }, createContainerRecord : function(params) {
	  //Expects containerId, dataRecNo, action
      var group = this.apz.scrMetaData.containersMap[params.containerId].group;
      var nodes;
      if (this.apz.isNull(group)) {
         nodes = this.apz.scrMetaData.containersMap[params.containerId].nodes;
      } else {
         nodes = this.apz.scrMetaData.groupsMap[group].nodes;
      }
      var noOfNodes = nodes.length;
      var node = "";
      var multiRec = this.apz.scrMetaData.containersMap[params.containerId].multiRec;
      if (noOfNodes > 0) {
         /////Create New data Records..
         for (var n = 0; n < noOfNodes; n++) {
            node = nodes[n];
            ////Call Set data
            if (params.action == "B") {
               var containerTracker = [];
               var dataPointers = [];
               params.dataPointers = dataPointers;
               params.tracker = containerTracker;
               this.setData(params);
            }
            ////Create New Record...The Last Flag(true) is to Ensure Force insert even if the record exists
            var forceFlag = false;
            if ((params.action == "A") || (params.action == "B")) {
               forceFlag = true;
            }
            var args = {};
            args.nodeId = node;
            args.dataRecNo = params.dataRecNo;
            args.force = forceFlag;
            this.createDataPointer(args);
            ////Manipulate Current Record..
            if (params.action == "B") {
               if (this.apz.scrMetaData.nodesMap[node].relType == "1:N") {
                  this.apz.scrMetaData.nodesMap[node].currRec = this.apz.scrMetaData.nodesMap[node].currRec + 1;
               }
            }
         }
      }
   }, removenestlist : function(containerId, rowNo) {
      if ( typeof (this.apz.scrMetaData.containersMap[containerId].childs) != "undefined" && this.apz.scrMetaData.containersMap[containerId].childs.length > 0) {
         var childLen = document.getElementById(containerId + "_row_" + rowNo).children.length;
         var parentNode = document.getElementById(containerId + "_row_" + rowNo);
         $(parentNode.children[0]).siblings('div').remove();
      }
   }, addRow : function(containerId, rowNo) {
      var id = containerId + "_row_" + rowNo;
      var rowExists = false;
      var rowVisible = false;
      var rowObj = document.getElementById(id);
      var deviceType = this.apz.deviceType;
      if (!this.apz.isNull(rowObj)) {
         ////Exists..Check the Class
         rowExists = true;
         if ($("#" + id).hasClass("ssp") || $("#" + id).hasClass("sno")) {
            rowVisible = false;
         } else {
            rowVisible = true;
         }
      } else {
         rowExists = false;
         rowVisible = false;
      }
      if (!rowExists) {
         ////Create a Row..
         var id_0 = containerId + "_row_0";
         if (rowNo == 0) {
            $("#" + id).removeClass("ssp");
            $("#" + id).removeClass("sno");
         } else {
            //Insert a new Row..
            if (deviceType == 'WIN8SURFACE' || deviceType == 'WIN8.1PHONE' || deviceType == 'WIN8.1SURFACE') {
               MSApp.execUnsafeLocalFunction(function() {
                  var firstRow = document.getElementById(id_0);
                  var parentNode = document.getElementById(id_0).parentNode;
                  var html = firstRow.outerHTML;
                  html = html.replace(new RegExp("rowno=\"0\"", 'g'), "rowno=\"" + rowNo + "\"");
                  html = html.replace(new RegExp("_0\"", 'g'), "_" + rowNo + "\"");
                  html = html.replace(new RegExp("_0'", 'g'), "_" + rowNo + "'");
                  html = html.replace(new RegExp("_0_", 'g'), "_" + rowNo + "_");
                  newRow = $(html)[0];
                  parentNode.appendChild(newRow);
	              if($("#"+containerId+"_table").hasClass('dataTable')){
	               	  $("#"+containerId+"_table").DataTable().row.add($(newRow)).draw(false);
	              }
                  this.removenestlist(containerId, rowNo);
                  //List inside List Changes
                  this.clearRow(containerId, rowNo);
                  $("#" + id).removeAttr("style");
                  $("#" + id).removeAttr("data-pos");
                  $("#" + id).removeClass("ssp");
                  $("#" + id).removeClass("sno");
                  $("#" + id).removeClass("mbsc-lv-item-swiping");
                  //List inside List Changes
               });
            } else {
               var firstRow = document.getElementById(id_0);
               var parentNode = document.getElementById(id_0).parentNode;
               var html = firstRow.outerHTML;
               html = html.replace(new RegExp("rowno=\"0\"", 'g'), "rowno=\"" + rowNo + "\"");
               html = html.replace(new RegExp("_0\"", 'g'), "_" + rowNo + "\"");
               html = html.replace(new RegExp("_0'", 'g'), "_" + rowNo + "'");
               html = html.replace(new RegExp("_0_", 'g'), "_" + rowNo + "_");
               newRow = $(html)[0];
               parentNode.appendChild(newRow);
               if($("#"+containerId+"_table").hasClass('dataTable')){
               	   $("#"+containerId+"_table").DataTable().row.add($(newRow)).draw(false);
               }
               this.removenestlist(containerId, rowNo);
               //List inside List Changes
               this.clearRow(containerId, rowNo);
               $("#" + id).removeAttr("style");
               $("#" + id).removeAttr("data-pos");
               $("#" + id).removeClass("ssp");
               $("#" + id).removeClass("sno");
               $("#" + id).removeClass("mbsc-lv-item-swiping");
               //List inside List Changes
            }
         }
      }
      if (!rowVisible) {
         //List inside List Changes Starts
         if (rowNo != 0)
            this.removenestlist(containerId, rowNo);
         //List inside List Changes Ends
         $("#" + id).removeClass("ssp");
         $("#" + id).removeClass("sno");
         $("#"+id).addClass($("#"+id).attr("animationtype") + " animated");
         this.apz.scrMetaData.containersMap[containerId].pageRows = parseInt(this.apz.scrMetaData.containersMap[containerId].pageRows) + parseInt(1);
         if (rowNo != 0) {
            rowObj = document.getElementById(id);
            this.apz.initRow(containerId, rowObj);
         }
      }
   }, deleteContainerRecord : function(containerId, dataRecNo) {
      var group = this.apz.scrMetaData.containersMap[containerId].group;
      var nodes;
      if (this.apz.isNull(group)) {
         nodes = this.apz.scrMetaData.containersMap[containerId].nodes;
      } else {
         nodes = this.apz.scrMetaData.groupsMap[group].nodes;
      }
      var noOfNodes = nodes.length;
      var node = "";
      var lmultiRec = this.apz.scrMetaData.containersMap[containerId].multiRec;
      if (noOfNodes > 0) {
         /////Create New data Records..
         for (var n = 0; n < noOfNodes; n++) {
            node = nodes[n];
            if (this.apz.scrMetaData.nodesMap[node].relType=="1:N") {
	            var parentPointer;
	            var parent = this.apz.scrMetaData.nodesMap[node].parent;
	            var parentCurrRec = -1;
	            if (this.apz.isNull(parent)) {
	               parentCurrRec = 0;
	            } else {
	               parentCurrRec = this.apz.scrMetaData.nodesMap[parent].currRec;
	            }
	            if (parentCurrRec >= 0) {
	               parentPointer = this.getDataPointer(parent, parentCurrRec);
	               var nodeName = this.apz.getNodeName(node);
	               if (parentPointer[nodeName]) {
	                  if (this.apz.scrMetaData.nodesMap[node].relType == "1:N") {
	                     if (parentPointer[nodeName].length > dataRecNo) {
	                        parentPointer[nodeName].splice(dataRecNo, 1);
	                     }
	                  } else {
	                     parentPointer[nodeName] = null;
	                  }
	               }
	            }
	         }
         }
      }
   }, removeRows : function(containerId) {
      var multiRec = this.apz.scrMetaData.containersMap[containerId].multiRec;
      var pageRows = this.apz.scrMetaData.containersMap[containerId].pageRows;
      var pageSize = this.apz.scrMetaData.containersMap[containerId].pageSize;
      var currRec = this.apz.scrMetaData.containersMap[containerId].currRec;
      var totalRecs = this.apz.scrMetaData.containersMap[containerId].totalRecs;
      var currPage = this.apz.scrMetaData.containersMap[containerId].currPage;
      var totalPages = this.apz.scrMetaData.containersMap[containerId].totalPages;
      var dataRecNo = -1;
      var deleteTdRows = 0;
      var currRecDeleted = false;
      var newCurrRec = currRec;
      var maxAvailableRec = -1;
      var rowNo = -1;
      var removeRow = true;
      if (currRec >= 0) {
         if(this.apz.isFunction(this.apz.app.PreRemoveRows)){
            removeRow = this.apz.app.PreRemoveRows(containerId);
            if (this.apz.isNull(removeRow)) {
               removeRow = true;
            }
         }
         if (removeRow) {
            if (multiRec == "Y") {
               currRecDeleted = this.isRowSelected(containerId, currRec);
               ////Save the Data First if Current Record is not deleted...
               if (!currRecDeleted) {
                  var containerTracker = [];
                  var dataPointers = [];
                  var args = {};
                  args.containerId  = containerId;
                  args.action = "D";
                  args.dataPointers = dataPointers;
                  args.tracker = containerTracker;
                  this.setData(args);
               }
               for (var r = pageRows - 1; r >= 0; r--) {
                  dataRecNo = this.getDataRec(containerId, r);
                  if (this.isRowSelected(containerId, r)) {
                     if (dataRecNo < currRec) {
                        newCurrRec = newCurrRec - 1;
                     }
                     if (dataRecNo < maxAvailableRec) {
                        maxAvailableRec = maxAvailableRec - 1;
                     }
                     deleteTdRows = deleteTdRows + 1;
                     this.removeRow(containerId, dataRecNo);
                     //////// to remove the checkbox checked to unchecked
                     this.unselectRow(containerId, r);
                     //////// ends here      all the changes
                  }
                  else {
                     if (dataRecNo > maxAvailableRec) {
                        maxAvailableRec = dataRecNo;
                     }
                  }
               }
               if (deleteTdRows > 0) {
                  if (pageRows == deleteTdRows) {
                     ///All Rows in the Current Page are Deleted..
                     if (currPage == totalPages) {
                        ////All Rows Deleted.. Go to First Row of the Previous Page
                        newCurrRec = ((currPage - 2) * pageSize);
                     } else if (currPage < totalPages) {
                        ////Firdst Row of the Next Page
                        newCurrRec = ((currPage - 1) * pageSize);
                     }
                     if (newCurrRec < -1) {
                        newCurrRec = -1;
                     }
                  } else {
                     ////Not All Records are Deleted..
                     if (currRecDeleted) {
                        ////Only Few Rows are Deleted...Go the Next Possible Record or Min row..
                        newCurrRec = maxAvailableRec;
                     } else {
                        ////Current ROw is Not Delted .. Hence adjusted Current Record wud become the new rec
                     }
                  }
                  if (this.apz.scrMetaData.containersMap[containerId].type == "TABLE") {
                     if ($('#' + containerId + '_0')[0].checked) {
                        $('#' + containerId + '_0')[0].checked = false;
                     }
                  }
               }
            } else {
               if (currRec == (totalRecs - 1)) {
                  newCurrRec = currRec - 1;
               } else {
                  newCurrRec = currRec;
               }
               this.removeRow(containerId, currRec);
            }
            var params = {};
            params.containerId = containerId;
            params.dataRecNo = newCurrRec;
            params.action = "D";
            this.goToRecord(params);
         }
         if(this.apz.isFunction(this.apz.app.postRemoveRows)){
            this.apz.app.postRemoveRows(containerId);
         }
      }
      this.apz.setTableHeight(containerId,false);
   }, removeRow : function(containerId, dataRecNo) {
      if (dataRecNo >= 0) {
         this.deleteContainerRecord(containerId, dataRecNo);
      }
   }, deleteRow : function(containerId, prowno) {
      var id = containerId + "_row_" + prowno;
      var rowExists = false;
      var rowVisible = false;
      var rowObj = document.getElementById(id);
      if (!this.apz.isNull(rowObj)) {
         ////Exists..Check the Class
         rowExists = true;
         if ($("#" + id).hasClass("sno") || $("#" + id).hasClass("ssp")) {
            rowVisible = false;
         } else {
            rowVisible = true;
         }
      } else {
         rowExists = false;
         rowVisible = false;
      }
      if (rowVisible) {
         $("#" + id).addClass("sno");
         this.apz.scrMetaData.containersMap[containerId].pageRows = this.apz.scrMetaData.containersMap[containerId].pageRows - 1;
      }
   }, changePage : function(containerId, ind, obj) {
      var checkChangePage = true;
      if(this.apz.isFunction(this.apz.app.preChangePage)){
         checkChangePage = this.apz.app.preChangePage(containerId, ind, obj);
         if (this.apz.isNull(checkChangePage)) {
            checkChangePage = true;
         }
      }
      if (checkChangePage) {
         var id = "";
         var newPage = 0;
         if (ind == "N") {
            newPage = this.apz.scrMetaData.containersMap[containerId].currPage + 1;
         } else if (ind == "P") {
            newPage = this.apz.scrMetaData.containersMap[containerId].currPage - 1;
         } else if (ind == "F") {
            newPage = 1;
         } else if (ind == "L") {
            newPage = this.apz.scrMetaData.containersMap[containerId].totalPages;
         } else if (ind == "S") {
            if (this.apz.scrMetaData.containersMap[containerId].paginationStyle == "PAGE2" || this.apz.scrMetaData.containersMap[containerId].paginationStyle == "PAGE3") {
               newPage = parseInt(this.apz.getElmValue(obj.id));
            } else {
               var cpid = containerId + "_cp";
               try {
                  newPage = parseInt(document.getElementById(cpid).value);
               } catch (e) {
                  newPage = this.apz.scrMetaData.containersMap[containerId].currPage;
               }
            }
         }
         if ((newPage > 0) && (newPage <= this.apz.scrMetaData.containersMap[containerId].totalPages) && (newPage != this.apz.scrMetaData.containersMap[containerId].currPage)) {
            ///Change the Page...
            var dataRecNo = (newPage - 1) * this.apz.scrMetaData.containersMap[containerId].pageSize;
            var params = {};
            params.containerId = containerId;
            params.dataRecNo = dataRecNo;
            params.action = "P";
            this.goToRecord(params);
         }
      }
      if(this.apz.isFunction(this.apz.app.postChangePage)){
         this.apz.app.postChangePage(containerId, ind, obj);
      }
   }, appendnestList : function(containerId, currRec) {
      var containerid = containerId + "_row_" + currRec;
      if ( typeof (this.apz.scrMetaData.containersMap[containerId].childs) != "undefined" && this.apz.scrMetaData.containersMap[containerId].childs.length > 0) {
         var parentNode = document.getElementById(containerid);
         var len = this.apz.scrMetaData.containersMap[containerId].childs.length;
         for ( i = 0; i < len; i++) {
            parentNode.appendChild(document.getElementById(this.apz.scrMetaData.containersMap[containerId].childs[i]));
         }
      }
   }, rowClicked : function(rowObj,event) {
      if (!this.apz.isNull(event) && !($(event.target).hasClass('dticon') || $(event.target).parent().hasClass('dticon') || $(event.target).parent().parent().hasClass('dticon'))) {
    	  var rowClicked = true;
         var rowId = rowObj.id;
         var ind = rowId.lastIndexOf("_row_");
         var containerId = rowId.substring(0, ind);
         var rowNo = this.apz.getInt(rowId.substring(ind + 5));
         var checkStrng = new Array();
         var radioStrng = new Array();
         if(this.apz.isFunction(this.apz.app.preRowClicked)){
            rowClicked = this.apz.app.preRowClicked(containerId, rowNo, event);
            if (this.apz.isNull(rowClicked)) {
               rowClicked = true;
            }
         }
         var contData = this.apz.scrMetaData.containersMap[containerId];
         if (rowClicked && !this.apz.isNull(contData) && this.apz.isNull(contData.searchIndex)) {
            var dataRecNo = this.getDataRec(containerId, rowNo);
            var currRec = contData.currRec;
            if (dataRecNo != currRec) {
               var params = {};
               params.containerId = containerId;
               params.dataRecNo = dataRecNo;
               params.action = "N";
               this.goToRecord(params);
               this.appendnestList(containerId, rowNo);
               //List inside List Changes
            }
         }
         if(this.apz.isFunction(this.apz.app.postRowClicked)){
            this.apz.app.postRowClicked(containerId, rowNo, event);
         }
         var parentContId = $("#"+containerId).attr("targetoverlayid");
         if(!this.apz.isNull(parentContId)){
           if(this.apz.isFunction(this.apz.app["overlayClicked_"+parentContId])){
               if (!this.apz.isNull(parentContId) && parentContId!=containerId) {
                  this.apz.app["overlayClicked_"+parentContId](parentContId, containerId, rowNo);
                  this.apz.searchRecords(containerId,"");
                  if ($("#"+containerId).find(".srh-inp").length>0) {
                     $("#"+containerId).find(".srh-inp").val("");
                  }
                  this.apz.closeOverlay($('#'+parentContId+"_dropdown")[0],containerId,"Y");
               }
            }
         }
      }
   }, changeRow : function(containerId, ind) {
      var changeRow = true;
      if(this.apz.isFunction(this.apz.app.preChangeRow)){
         changeRow = this.apz.app.preChangeRow(containerId, ind);
         if (this.apz.isNull(changeRow)) {
            changeRow = true;
         }
      }
      if (changeRow) {
         var id = "";
         var newRec = 0;
         if (ind == "N") {
            newRec = this.apz.scrMetaData.containersMap[containerId].currRec + 1;
         } else if (ind == "P") {
            newRec = this.apz.scrMetaData.containersMap[containerId].currRec - 1;
         } else if (ind == "F") {
            newRec = 0;
         } else if (ind == "L") {
            newRec = this.apz.scrMetaData.containersMap[containerId].totalRecs - 1;
         } else if (ind == "S") {
            var crid = containerId + "_cr";
            try {
               newRec = parseInt(document.getElementById(crid).innerHTML) - 1;
            } catch (e) {
               newRec = this.apz.scrMetaData.containersMap[containerId].currRec;
            }
         }
         if ((newRec >= 0) && (newRec < this.apz.scrMetaData.containersMap[containerId].totalRecs) && (newRec != this.apz.scrMetaData.containersMap[containerId].currRec)) {
            ///Change the Row...
            var dataRecNo = newRec;
            var params = {};
            params.containerId = containerId;
            params.dataRecNo = dataRecNo;
            params.action = "N";
            this.goToRecord(params);
         }
      }
      if(this.apz.isFunction(this.apz.app.postChangeRow)){
         this.apz.app.postChangeRow(containerId, ind);
      }
   }, setContainerCounters : function(pcontainer) {
      var setContainerCounters = true;
       if(this.apz.isFunction(this.apz.app.preSetContainerCounters)){
           setContainerCounters = this.apz.app.preSetContainerCounters(pcontainer);
           if (this.apz.isNull(setContainerCounters)) {
               setContainerCounters = true;
           }
       } 
      //Changes for paginationstyle PAGE2
      if (this.apz.scrMetaData.containersMap[pcontainer].paginationStyle == "PAGE2") {
         this.apz.showPageStyle2Controls(pcontainer);
      }else if (this.apz.scrMetaData.containersMap[pcontainer].paginationStyle == "PAGE3") {
         this.apz.showPageStyle3Controls(pcontainer);
      } else {
         if (setContainerCounters) {
            var lmultirec = this.apz.scrMetaData.containersMap[pcontainer].multiRec;
            if (lmultirec == "Y") {
               var lcpid = pcontainer + "_cp";
               var ltpid = pcontainer + "_tp";
               var lprid = pcontainer + "_pr";
               try {
                  document.getElementById(lcpid).value = this.apz.scrMetaData.containersMap[pcontainer].currPage;
                  document.getElementById(ltpid).innerHTML = this.apz.scrMetaData.containersMap[pcontainer].totalPages;
                  document.getElementById(lprid).value = this.apz.scrMetaData.containersMap[pcontainer].pageRows;
               } catch (e) {
               }
            } else {
               var lcrid = pcontainer + "_cr";
               var ltrid = pcontainer + "_tr";
               try {
                  document.getElementById(lcrid).value = (this.apz.scrMetaData.containersMap[pcontainer].currRec + 1);
                  document.getElementById(ltrid).innerHTML = this.apz.scrMetaData.containersMap[pcontainer].totalRecs;
               } catch (e) {
               }
            }
         }
      }
      if(this.apz.isFunction(this.apz.app.postSetContainerCounters)){
         this.apz.app.postSetContainerCounters(pcontainer);
      }
   } , getSelectedRows : function(containerId) {
      var rows = this.apz.scrMetaData.containersMap[containerId].pageRows;
      var value = new Array();
      for (var i = 0; i < rows; i++) {
         var id = containerId + '_row_' + i;
         var className = $('#' + id)[0].className;
         if (className.indexOf('selected') != -1) {
            value[id] = $('#' + id);
         }
      }
      return value;
   }, appendData : function(newData) {
      for (key in newData) {
         this.scrdata[key] = newData[key];
      }
   }, loadJsonData : function(fileName, appId) {
	   /* Params contains the below values
	       *** fileName, appId ***
	   */
      var filePath = this.apz.getDataFilesPath(appId) + "/" + fileName + ".json";
      var content = this.apz.getFile(filePath);
      if (!this.apz.isNull(content)) {
         content = JSON.parse(content);
         this.appendData(content);
         this.loadData(null);
      }
   }, selectRow : function(currObj, event) {
		if(event.ctrlKey || event.metaKey) {
			$(currObj).toggleClass('selected');
			// throw 'This should be an uncaught exception.';
		}
   }
};
