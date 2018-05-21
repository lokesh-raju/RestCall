Apz.prototype.accordionAction = function(obj) {
	var proceed = true;
	if(this.isFunction(this.app.preAccordionAction)){
		proceed = this.app.preAccordionAction(obj);
	}
	if (proceed != false) {
		var jqueryObj = $(obj);
		if(jqueryObj.parent().find('.height-transition:first').hasClass("height-transition-hidden")){
            jqueryObj.parent().siblings().removeClass("active");
			jqueryObj.parent().addClass("active");
			jqueryObj.attr({'aria-selected':'true','aria-expanded':'true'});
			this.slideDownTransition(obj);
		}else{
			this.slideUpTransition(obj);		
			jqueryObj.parent().removeClass("active");
			jqueryObj.attr({'aria-selected':'false','aria-expanded':'false'});
		}
	}
	if(this.isFunction(this.app.postAccordionAction)){
		this.app.postAccordionAction(obj);
	}
}, Apz.prototype.tabAction = function(obj) {
    var proceed = true;
    var activeTabCont = [];
    var tabobj, ulObj;
    if (this.isFunction(this.app.preTabAction)) {
        proceed = this.app.preTabAction(obj);
    }
    if (proceed != false) {
        var jqueryObj = $(obj);
        if (jqueryObj.parent().hasClass('acco')) {
            if (jqueryObj.parent().hasClass('acco') && !jqueryObj.next('div').hasClass('active')) {
                jqueryObj.attr({'aria-selected':'true','aria-expanded':'true'});
                jqueryObj.next('div').addClass('active');
                var indx = jqueryObj.parent().parent().find('.acco').index(jqueryObj.parent());
                var liObj = jqueryObj.parents('.tabs-ctr:first').siblings('.tabs').find('li')[indx];
                $(liObj).siblings('li').removeClass('current').attr({'aria-selected':'false','aria-expanded':'false'});
                $(liObj).addClass('current').attr({'aria-selected':'true','aria-expanded':'true'});
                jqueryObj.addClass('current');
                jqueryObj.parent().siblings('div').children('ul').removeClass('current').attr({'aria-selected':'false','aria-expanded':'false'});
                jqueryObj.parent().siblings('div').children('.active').removeClass('active');
                activeTabCont = jqueryObj.next('div');

            }
        }
        //for desktop
        else {
            if (!(jqueryObj).hasClass("current")) {
                jqueryObj.addClass("current");
                jqueryObj.siblings('li').removeClass("current").attr({'aria-selected':'false','aria-expanded':'false'});
                jqueryObj.attr({'aria-selected':'true','aria-expanded':'true',})
            }
            var parentDiv = jqueryObj.parents('div.pst-tabs:first');
            if (parentDiv.hasClass('nrsp')) {
                tabobj = jqueryObj.closest('div.pst-tabs').children('div.tabcontent');
                tabobj.removeClass('active').attr('aria-hidden','true');
                activeTabCont = tabobj.eq(jqueryObj.index());
                activeTabCont.addClass('active').attr('aria-hidden','false');
            } else {
                tabobj = jqueryObj.closest('div.pst-tabs').children('div.tabs-ctr').children('div.acco').children('div.tabcontent');
                ulObj = jqueryObj.parent('ul:first').siblings('div.tabs-ctr').find('div.acco').children('ul');
                tabobj.removeClass('active').attr('aria-hidden','true');
                tabobj.parent('.acco').attr('aria-hidden','true');
                ulObj.removeClass('current').attr({'aria-selected':'false','aria-expanded':'false'});
                activeTabCont = tabobj.eq(jqueryObj.index());
                activeTabCont.attr('aria-hidden','false').parent('.acco').attr('aria-hidden','false');
                activeTabCont.addClass('active').attr('aria-hidden','false').siblings("ul").addClass('current').attr({'aria-selected':'true','aria-expanded':'true'});
            }
        }
    }
    if (activeTabCont.length > 0) {
        this.initFixedHeaderTables(activeTabCont.find('table[id].fixedheader:visible'));
        this.initDataTables(activeTabCont.find('table[id].responsive:visible'));
    }
    if (this.isFunction(this.app.postTabAction)) {
        this.app.postTabAction(obj);
    }
}, Apz.prototype.setTableHeight = function(containerId,destroy) {
      if(this.scrMetaData.containersMap[containerId].type == "TABLE" && !$("#"+containerId+"_table").hasClass("responsive")) {
         if($(document.getElementById(containerId)).hasClass("pri")) {
            var tableObj = document.getElementById(containerId+"_table");
            $tableObj = $(tableObj);
            $tableDiv = $(document.getElementById(containerId)).find('.tabl-ctr');
            var tableHeight = $tableObj.height();
            var usrHeight = $tableObj.data("tableheight");
            usrHeight = parseFloat(usrHeight);
            if(tableHeight < usrHeight) {
               $tableDiv.height(tableHeight);
            } else {
               $tableDiv.height(usrHeight);
            }
         }
         this.initFixedHeaderTable($("#"+containerId+"_table[id].fixedheader:visible"), destroy);
      }
   }, Apz.prototype.rowSelectorClicked = function (event) {
      event.stopPropagation();
      if(this.isFunction(this.app.rowSelectorClicked)){
         this.app.rowSelectorClicked(event);
      }
   }, Apz.prototype.showPageStyle2Controls = function(container) {
      var st = 0;
      var le = 0;
      var tp = this.scrMetaData.containersMap[container].totalPages;
      var cp = this.scrMetaData.containersMap[container].currPage;
      var diff = cp - 5;
      for (var i = 1; i <= 5; i++) {
           if ((i <= tp) && cp <=tp) {
               this.show(container + '_' + i + '_btn');
               if(cp>5){
                   this.setElmValue(container + '_'+ i +'_btn',diff+i);
               }else{
                   this.setElmValue(container + '_'+ i +'_btn',i);
               }
           } else {
               this.hide(container + '_' + i + '_btn');
           }
       }
      ///// Control Buttons
      if (tp <= 0) {
         this.hide(container + '_first_btn');
         this.hide(container + '_prev_btn');
         this.hide(container + '_next_btn');
         this.hide(container + '_last_btn');
         ////Hide all five Buttons
      }
      else {
         this.show(container + '_first_btn');
         this.show(container + '_prev_btn');
         this.show(container + '_next_btn');
         this.show(container + '_last_btn');
         /////Adjust Page Numbers
         var ps = this.getElmValue(container + '_1_btn');
         var pe = this.getElmValue(container + '_5_btn');
         var ls = cp;
         if (cp > pe) {
            le = cp;
            ls = le - 4;
         } else if (cp < ps) {
            ls = cp;
            le = ls + 4;
         }
      }
      $('#'+ container + '_pagination_li .active').removeClass("active");
      $('#' + container + '_' + ((cp+1)-parseInt(ps))+ '_btn').addClass('active');
      //$('#'+container + '_pagination_li .pri').addClass("tsp");
      //$('#'+container + '_pagination_li .pri').removeClass("pri");     
      //$('#' + container + '_' + ((cp+1)-parseInt(ps))+ '_btn').removeClass('tsp');
      //$('#' + container + '_' + ((cp+1)-parseInt(ps))+ '_btn').addClass('pri');

   }, Apz.prototype.showPageStyle3Controls = function(container) {
      var st = 0;
      var le = 0;
      var tp = this.scrMetaData.containersMap[container].totalPages;
      var cp = this.scrMetaData.containersMap[container].currPage;
      var diff = cp - 4;
      var lastPgNo = parseInt($("#"+container + '_1_btn').parent().children('a:visible:last').text());
      if(cp != tp || (cp == tp && tp == 1) || (cp == tp && tp != lastPgNo)){
          if (cp == tp && tp != lastPgNo) {
            diff = cp - 5;
          }
         for (var i = 1; i <= 5; i++) {
            if ((i <= tp) && cp <=tp) {
               this.show(container + '_' + i + '_btn');
               if(cp>4){
                  this.setElmValue(container + '_'+ i +'_btn',diff+i);
               }else{
                  this.setElmValue(container + '_'+ i +'_btn',i);
               }
            } else {
               this.hide(container + '_' + i + '_btn');
            }
         }
      }
      ///// Control Buttons
      if (tp > 0) {
        /////Adjust Page Numbers
         var ps = this.getElmValue(container + '_1_btn');
         var pe = this.getElmValue(container + '_5_btn');
         //var ls = cp;
         /*if (cp > pe) {
            le = cp;
            ls = le - 4;
         } else if (cp < ps) {
            ls = cp;
            le = ls + 4;
         }*/
      }
	  //$('#'+container + '_pagination_li .ebtn-primary').addClass("ebtn-info");
      $('#'+container + '_pagination_li .active').removeClass("active");	  
	  //$('#' + container + '_' + ((cp+1)-parseInt(ps))+ '_btn').removeClass('ebtn-info');
      $('#' + container + '_' + ((cp+1)-parseInt(ps))+ '_btn').addClass('active');
      var pageSize = this.scrMetaData.containersMap[container].pageSize;
	  var lastRec = this.scrMetaData.containersMap[container].currPage * pageSize;
	  var totalRecs = this.scrMetaData.containersMap[container].totalRecs;
	  if(lastRec > totalRecs){
		 lastRec = totalRecs;
	  }
     var currentRec = "";
     var currRec = this.scrMetaData.containersMap[container].currRec;
     if (currRec== -1) {
      currentRec = 0;
     } else {
         var rem = (currRec)%pageSize;
        if (rem == 0){
         currentRec = currRec + 1;
        } else {
         currentRec = currRec - rem + 1;
        }
     }
     if (currentRec<=0) {
      currentRec = 1;
     }
	  $('#'+container + '_info').text('Showing ' +currentRec+ ' to ' +lastRec+ ' of ' +totalRecs+' entries');	
   }, Apz.prototype.closeResponsiveTableRows = function(tableObj){
	   var tableData = tableObj.DataTable();
	   tableObj.find("tr.child").each(function(){
	   	 var rowObj = $(this).prev();
         var index = rowObj.index();
         var row = tableData.row(index);
         var prevIcon = rowObj.find("[targetclass]");
         if(prevIcon!=="" && prevIcon!==undefined && prevIcon.length!==0){ 
            var iconName = prevIcon.attr("targetclass");
            var svgIcon = prevIcon.find("svg");
            if(svgIcon.length > 0) {
               var svgIconName = prevIcon.find("svg").attr("class").replace("icon","").trim().split(" ")[0];
               prevIcon.find("svg").removeClass(svgIconName).addClass(iconName);
               prevIcon.find("use").attr("xlink:href","#"+iconName);
               prevIcon.attr("targetclass",svgIconName);
            }
         }
   	     row.child.hide();
   	     rowObj.removeClass("parent");
	   })
   }, Apz.prototype.breadCrumbAction = function(obj) {
		var proceed = true;
		if(this.isFunction(this.app.prebreadCrumbAction)){
			proceed = this.app.prebreadCrumbAction(obj);
		}
		if(proceed != false){
			var jqueryObj = $(obj);
			if (!(jqueryObj).hasClass("active")) {
				jqueryObj.addClass("active");
			    jqueryObj.siblings('li').removeClass("active");
			}
		}
		if(this.isFunction(this.app.postbreadCrumbAction)){
			this.app.postbreadCrumbAction(obj);
		}
	}, Apz.prototype.collapsibleAction = function(obj) {
		if(this.isFunction(this.app.preCollapsibleAction)){
			this.app.preCollapsibleAction(obj);
		}
		if($(obj).parent().find('.height-transition').hasClass("height-transition-hidden")){
			$(obj).attr({'aria-selected':'true','aria-expanded':'true'});
			this.slideDownTransition(obj);
		}else{
			this.slideUpTransition(obj);
			$(obj).attr({'aria-selected':'false','aria-expanded':'false'});
		}
		if(this.isFunction(this.app.postCollapsibleAction)){
			this.app.postCollapsibleAction(obj);
		}
	}, Apz.prototype.equalizeColumns = function(proc) {
		return $('#scr__'+proc.appId+'__'+ proc.scr + '__main .grb.equalize').each(function() {
			var $row, collapsed, tallest;
			$row = $(this);
			tallest = 0;
			collapsed = false;
			$row.children().each(function(i) {
				var $this;
				$this = $(this);
				$this.css('minHeight', '1px');
				collapsed = $this.outerWidth() === $row.outerWidth();
				if (!collapsed) {
					if (!$this.hasClass('equal')) {
						$this.addClass('equal');
					}
					if ($this.outerHeight() > tallest) {
						tallest = $this.outerHeight();
					}
				}
			});
			if (!collapsed) {
				return $row.children().css('min-height', tallest);
			}
		});
	},Apz.prototype.initTables = function(proc) {
		this.initDataTables($('#scr__' +proc.appId+'__'+ proc.scr + '__main .responsive:visible'), true);
		this.initFixedHeaderTables($('#scr__'+proc.appId+'__' + proc.scr + '__main [id].fixedheader:visible'));
	}, Apz.prototype.initDataTables = function(obj, destroy) {
		var jqueryObj = $(obj);
		apzObj = this;
		if(jqueryObj.length > 0) {
			jqueryObj.each(function() {
				apzObj.initDataTable($(this),destroy);
				if(!$(this).parents(".crb-tabl:first")[0].__resizeListeners__) {
					var resizeTable = apzObj.resizeTableHandler($(this).parents(".crb-tabl:first")[0]);
                	apzObj.addResizeListener($(this).parents(".crb-tabl:first")[0],resizeTable );
				}
			});
		}
	}, Apz.prototype.initDataTable = function(obj, destroy) {
		var jqueryObj = $(obj);
		var tableDiv = jqueryObj.parents(".crb-tabl:first");
		if(tableDiv.find('object').length > 0) {
	    	var backupObj = tableDiv.parent().find('object');
	    	tableDiv.find('object').remove();
	    }
	    var apzObj = this;
	    if (jqueryObj.length > 0) {
	        	if(jqueryObj.is(":visible") && jqueryObj.hasClass('responsive')) {
	        		var proceed = true;
		            if (this.isNull(destroy)) {
		                destroy = false;
		            }
		            if ($.fn.DataTable.isDataTable(jqueryObj[0])) {
		                if (destroy) {
		                    jqueryObj.DataTable().destroy();
		                } else {
		                    proceed = false;
		                }
		            } else if(jqueryObj.find('tbody tr').length == 0) {
		            	proceed = false;
		            }
		            if (proceed) {
		                var table = jqueryObj.DataTable({
		                    paging: false,
		                    ordering: false,
		                    info: false,
		                    searching: false,
		                    searchable: false
		                });
		                setTimeout(function(){
                      tableDiv.attr("oldWidth",tableDiv.find(".responsive").width());
		                },100);
		                jqueryObj.find('tbody').off("click.apzevent").on('click.apzevent', 'td.dticon', function() {
		                    var tablDiv = $(this).closest('.crb-tabl');
		                    var tr = $(this).closest('tr');
		                    var table = $(this).closest('table').DataTable();
		                    var row = table.row(tr);
		                    var iconObj = $(this).children('svg');
		                    if (iconObj.length > 0) {
		                        iconObj.removeClass("px24");
		                        var newTargetCls = iconObj.attr('class').replace("icon", "").trim();
		                        var oldTargetClass = $(this).attr("targetclass");
		                        if (row.child.isShown() && oldTargetClass !== "") {
		                            var previousElement = $(this).parent().siblings().find("[targetclass]");
		                            previousElement.find("use").attr("xlink:href", "#" + newTargetCls);
		                            previousElement.find("svg").removeClass(oldTargetClass).addClass(newTargetCls);
		                            previousElement.attr('targetclass', oldTargetClass);
		                        }
		                        $(this).attr("targetclass", newTargetCls);
		                        $(this).html('').append('<svg class="icon ' + oldTargetClass + ' px24" aria-hidden="true"><use xlink:href="#' + oldTargetClass + '"></use></svg>');
		                    }
		                    if ($(tr).next('tr').hasClass('child')) {
		                        var childTd = $(tr).next('tr').children();
		                        var lchilds = childTd.children().children();
		                        if (lchilds.length > 0) {
		                            var childElmObjs = $(lchilds).find('[rowno]');
		                            $(childElmObjs).each(function() {
		                                var targetObj = $("#" + this.id);
		                                childTd.addClass(targetObj.closest("td").attr("class"));
		                                apzObj.setObjValue(this, apzObj.getObjValue(targetObj[0]));
		                            })
		                        }
		                    }
		                    //tablDiv.attr("tableaction","tableaction");
		                    if (apzObj.isFunction(apzObj.app.postExpandAction)) {
		                        apzObj.app.postExpandAction(this)
		                    }
		                });
		            } else if($.fn.DataTable.isDataTable(jqueryObj[0])) {
		            	jqueryObj.DataTable().columns.adjust().draw();
		            }
	        	}
	    }
	    tableDiv.append(backupObj);
}, Apz.prototype.initFixedHeaderTables = function(obj) {
	var apzObj = this;
	var jqueryObj = $(obj);
	if(jqueryObj.length > 0) {
		jqueryObj.each(function() {
			apzObj.setTableHeight($(this).parents(".crb-tabl:first").attr("id"),false);
			if(!$(this).parents(".crb-tabl:first")[0].__resizeListeners__) {
				var resizeTable = apzObj.resizeTableHandler($(this).parents(".crb-tabl:first")[0]);
    			apzObj.addResizeListener($(this).parents(".crb-tabl:first")[0], resizeTable);
			}
		});
	}

}, Apz.prototype.initFixedHeaderTable = function(obj,destroy) {
		var jqueryObj = $(obj); 
		if(jqueryObj.length > 0 && jqueryObj.is(":visible") && jqueryObj.hasClass('fixedheader')) {
			var tblWrap = jqueryObj.parents(".crb-tabl:first");
			if (destroy) {
				jqueryObj.fixedHeaderTable('destroy');
			}else {
				tblWrap.find('.fht-cell').remove();
            }
			tblWrap.attr("oldWidth",tblWrap.width());
			jqueryObj.fixedHeaderTable({});
		}
}, Apz.prototype.initCarousels = function(proc) {
		$('#scr__'+proc.appId+'__' + proc.scr + '__main .swiper-container').each(function () {
			var loop = $(this).attr('data-loop') == "y" ? true : false;
			var swiper = new Swiper('#'+this.id, {
		            pagination: '.swiper-pagination',
		            nextButton: '.swiper-button-next',
		            prevButton: '.swiper-button-prev',
		            slidesPerView: 1,
		            paginationClickable: true,
		            spaceBetween: 30,
		            loop: loop,
		            autoplay: false,
		            autoHeight: false,
		            observer: true,
					observeParents: true
		        });
		});
}, Apz.prototype.initFloatingLabels = function(proc){
		$('#scr__'+proc.appId+'__' + proc.scr + '__main .flbl input').each(function () {
			  function updateText(event) {
                var input = $(this);
                setTimeout(function () {
                    var val = input.val();
					
                    if (val != "")
                        input.parents('.flbl:first').addClass("flbl-float");
                    else
                        input.parents('.flbl:first').removeClass("flbl-float");
                }, 1)
            }		
			$(".flbl input").keydown(updateText);
            $(".flbl input").change(updateText);		
			$(this).trigger("change");
		});
		
		$('#scr__'+proc.appId+'__' + proc.scr + '__main .flbl textarea').each(function () {
			  function updateText(event) {
                var input = $(this);
                setTimeout(function () {
                    var val = input.val();
					
                    if (val != "")
                        input.parent().parents('.flbl:first').addClass("flbl-float");
                    else
                        input.parent().parents('.flbl:first').removeClass("flbl-float");
                }, 1)
            }
					
			$(".flbl textarea").keydown(updateText);
            $(".flbl textarea").change(updateText);
		
			$(this).trigger("change");
		});
		
	}, Apz.prototype.updateFloatingClass = function(obj){
		
	},
	Apz.prototype.initDraggables = function(proc) {
		// Move the element by the amount of change in the mouse position
			var element = jQuery('#scr__'+proc.appId+'__' + proc.scr + '__main .modal-draggable');
	        var move = function (event) {
		        if (element.data('mouseMove')) {
		        	var changeX = event.clientX - element.data('mouseX');
		            var changeY = event.clientY - element.data('mouseY');

		            var newX = parseInt(element.css('left')) + changeX;
		            var newY = parseInt(element.css('top')) + changeY;

		            element.css('left', newX);
		            element.css('top', newY);

		            element.data('mouseX', event.clientX);
		            element.data('mouseY', event.clientY);
		        }
	        }
	        element.mousedown(function (event) {
	            element.data('mouseMove', true);
	            element.data('mouseX', event.clientX);
	            element.data('mouseY', event.clientY);
	        });
	        element.parents(':last').mouseup(function () {
	            element.data('mouseMove', false);
	        });
	        element.mouseout(move);
	        element.mousemove(move);
	}, Apz.prototype.handleMenus = function(proc) {
		if (proc.type == "PG") {
			jQuery('.topnav ul').hide();
		}
		if(jQuery.fn.accordion){
			jQuery("#scr__"+proc.appId+'__'+ proc.scr + "__main .topnav").accordion({
	            accordion: false,
	            speed: 500,
	            closedSign: '<svg class="icon icon-downarrow px12" aria-hidden="true"><use xlink:href="#icon-downarrow"></use></svg>',
	            openedSign: '<svg class="icon icon-uparrow px12" aria-hidden="true"><use xlink:href="#icon-uparrow"></use></svg>'
	        });
		}
	}, Apz.prototype.closeMenu = function() {
		$("#myModal").css({ 'display': "none" });
	}, Apz.prototype.handleLov = function() {
		jQuery("#lovModal").hide();
		jQuery("#lovwindow").hide();
		jQuery("#lovtoggle").show();
	}, Apz.prototype.handlePopupMenu = function(elm) {
		var menuObj = $(elm).closest("li").find("ul");
		// // Reset Previous seted css values
		menuObj.css("top", "");
		menuObj.css("bottom", "");
		menuObj.css("left", "");
		menuObj.css("right", "");
		var elmheight = $(elm).parent().height();
		var menuOffset = menuObj.offset();
		var menuHeight = menuObj.height();
		var menuWidth = menuObj.width();
		var parent;
		if ($("#page-body").find(menuObj).length > 0) {
			parent = $("#page-body");
		} else if ($("#footer").find(menuObj).length > 0) {
			parent = $("#footer");
		} else if ($("#header").find(menuObj).length > 0) {
			parent = $("#header");
		} else {
			// parent = $("#sidebar");
		}
		if (parent != undefined) {
			var parentoffset = parent.offset();
			var parentheight = parent.height();
			var parentwidth = parent.width();
			if ((menuOffset.top + menuHeight) > parentheight) {
				menuObj.css("top", "auto");
				menuObj.css("bottom", elmheight + "px");
			}
			if ((menuOffset.left + menuWidth) > parentwidth) {
				menuObj.css("left", "auto");
				menuObj.css("right", "0px");
			}
		}
	}, Apz.prototype.initDependentWidgets = function(proc) {
		// initi Data Tables
		this.initTables(proc);
	}, Apz.prototype.init = function(proc) {
		this.setScreenProps(proc);
		// handles tabs
		this.handleMenus(proc);
		// // handle LOV
		this.handleLov();
		// // Inti carousel
		this.initCarousels(proc);
		// // Init FloatingLabels
		this.initFloatingLabels(proc);
		// inti Calender
		this.handleCalendar();
		// inti Controls
		this.initControls(proc);
		// // init Modal Draggables
		this.initDraggables(proc);
		/////Sidebar push for open condition on page load
		if (proc.type != "CF" && proc.type != "SS") {
			this.initSidebarState();
		}
		this.initMenus(proc);
		////equalizing columns
		this.equalizeColumns(proc);
		this.marginSetForSideBar();
		//// Init table search
		this.initTableSearch(proc);
		//this.initOneTimeEvents();
		
		this.initPaginationDropdown();
    this.initModalEvent(proc);

		$(document).bind('keydown', function(e) {
			if (e.which == 27 && jQuery("#lovModal").css("display") != "none") {
				jQuery("#lovModal").toggle(0);
				jQuery("#lovwindow").slideToggle(200);
				e.stopPropagation();
			}
			if (e.which == 13 && jQuery("#lovModal").css("display") != "none") {
				if (e.target.id == "idFilter") {
					$(".icon-search").parent().trigger("click");
					e.stopPropagation();
				}
			}
		});
		var apzObj = this;
		$('#scr__'+proc.appId+'__'+ proc.scr + '__main .addedAuderoCM').click(function(event) {
			var id = this.id;
			var obj = this.scrMetaData.elmsMap[id].contextmenuid;
			var contxtId = obj + "_main_div";
			apzObj.handleContextMenu(contxtId, event); // // APZFIX
		});
		var headerObj = document.getElementById('header');
		/*if (headerObj) {
			headerObj.onclick = function(event) {
				apzObj.handleHeader(event);
			}
		}*/
        // Event Attatching has been moved inside the click on document in the same function, checking if the id is header

		//////tooltip init
		$('#scr__'+ proc.appId +'__'+ proc.scr + '__main .tooltipcls').tooltip({
			align : 'top'
		});
        $('#scr__' + proc.appId +'__'+ proc.scr + '__main .tooltipcls').keydown(function() {
          $('.tooltip-inner').parent(".tooltip").fadeOut(3000);
        });
        $('#scr__' + proc.appId + '__' + proc.scr + '__main .tooltipcls').click(function() {
          $('.tooltip-inner').parent(".tooltip").fadeOut(0);
        });

	}, Apz.prototype.setScreenProps = function(proc) {
		var scrProps = this.scrMetaData.scrProps;
		if(scrProps){
			var $body = $("body");
            if(proc.type == "PG"){ 
				var $html = $("html");
				var bgColor = "";
				if(!this.isNull(scrProps[1])){
					bgColor = "#"+scrProps[1];
				}
				$body.find(".rolepage").removeClass().addClass(scrProps[0] +" rolepage");			
				$body.css("background-color",bgColor);
				if(this.defaultTheme !== this.theme){
			        var bgimg = scrProps[2].split("/");
			        bgimg.splice(-3,1,this.theme);
			        scrProps[2] = bgimg.join("/");
			    }
				$body.css("background-image",scrProps[2]);
				$body.css("background-repeat",scrProps[3]);
				$body.css("background-attachment",scrProps[4]);
				$body.css("background-position",scrProps[5]);
				$body.css("background-size",scrProps[6]);
				$body.find(".rolepage").addClass(scrProps[7]);
				$body.removeClass('mnt mns mnp pri sec ter').addClass(scrProps[8]);
            } else {
                if(!this.isNull(scrProps[8])) {
                	if (scrProps[8].indexOf('mnt') != -1 || scrProps[8].indexOf('mns') != -1 || scrProps[8].indexOf('mnp') != -1) {
                		$body.removeClass('mnt mns mnp');
                	}
                	$body.addClass(scrProps[8]);
                }
            }
		}
	}, Apz.prototype.initMenus = function(proc) {
		var apzObj = this;
		var $scr = $('#scr__'+proc.appId+'__'+ proc.scr + '__main');
		if ($scr.find('.crt-hmnu').length>0 || $scr.find('.crt-vmnu').length>0) {
			if ($(window).width()>700) {
				var resp = false;
			} else {
				var resp = true;
			}
			if ($scr.find('.crt-hmnu').length>0) {
				$scr.find('.crt-hmnu').each(function(){
					//var menuId = $(this).attr('id');
					apzObj.initHorVerMenus(this,"topbar",resp);
				});
			}
			if ($scr.find('.crt-vmnu').length>0) {
				$scr.find('.crt-vmnu').each(function(){
					//var menuId = $(this).attr('id');
					apzObj.initHorVerMenus(this,"sidebar",resp);
				});
			}
		}
	}, Apz.prototype.initOneTimeEvents = function() {
		var apzObj = this;
		$('html').on('click.apzevent',function(e){
			/// For closing table search field
            if($(e.target).parents('.srh:first').hasClass('srh-open')){
                $('.srh').not($(e.target).parents('.srh:first')).removeClass('srh-open');
            } else {
            	$('.srh').removeClass('srh-open');
            }
            //// Closing Context Menu
            if($('.context-open').length > 0) {
				var tagname = $(e.target).prop("tagName");
			    if($(e.target).closest('.context-open').length > 0 && ((tagname == "LI" || tagname == "A") && $(e.target).find('i').length > 0) || (tagname == "SPAN") || (tagname == "I")) {
   
			    } else {
			    	e.preventDefault();
				    $('.context-open').fadeOut(500);
				    $('.context-open').removeClass("context-open");
			    }
			} else if ($(e.target).closest("#header").length > 0) { 
				apzObj.handleHeader(e);
			}
			// Hide on click outside for dropdown
			if ($(e.target).parents('.drdn-open').length==0) {
				if ($(e.target).hasClass('drdn-open')) {
					$('.drdn-open').not(e.target).each(function(){
			            if ($('.drdn-extspan').length>0) {
			              $("#"+$('.drdn-extspan').children().attr('original-id')+"_ext").append($('.drdn-extspan').children());
			            }
			            $(this).removeClass('is-open drdn-open');
			        });
				} else {
			        if ($('.drdn-extspan').length>0) {
			        	$("#"+$('.drdn-extspan').children().attr('original-id')+"_ext").append($('.drdn-extspan').children());
			        }
			        $('.drdn-open').removeClass('is-open drdn-open');
				}
        		$(".drdn-extspan").remove();
			}
			// Hide on outside click for popover
			if ($(".popover").length>0) {
				var proceed = true;
				if ($(e.target).parents("[aria-describedby]").length>0) {
					proceed = false;
				}
				if(proceed && !$(e.target).attr('aria-describedby') && $(e.target).parents(".popover").length==0){
	            	if ($(".popover").length > 0) {
	            		$(".popover").each(function(){
	            			if ($(this).data('bs.popover')!=undefined) {
	            				$(this).popover('hide').data('bs.popover').inState.click = false;
	            			}
	            		});
	            	}
	            }
			}
        });
		var windowObj = $(window);
		var setDrdnPos = function() {
			var cloneElm = $('.drdn-extspan');
			var $obj = $(document.getElementById(cloneElm.children().attr('original-id')));
			var cloneHt = cloneElm.children().height();
			var cloneWidth = $obj.parent().width();
			var objOffset = $obj.offset();
			var clonePosTop = objOffset.top;
			if ((objOffset.top + $obj.outerHeight() + cloneHt > windowObj.innerHeight() + windowObj.scrollTop()) && (objOffset.top>cloneHt)) {
				clonePosTop = objOffset.top - cloneHt - 10;
			} else {
				clonePosTop = objOffset.top + $obj.outerHeight() + 5;
			}
			$(cloneElm).css({
				width:cloneWidth,
				position:'absolute',
				top: clonePosTop + 'px',
				left: objOffset.left + 'px'
			});
		}
		windowObj.off('resize.apzevent').on('resize.apzevent',function() {
	        if ($('.drdn-extspan').length>0) {
				setDrdnPos();
	        }
		});
		window.addEventListener("scroll",function(){
			if ($('.drdn-extspan').length>0) {
				setDrdnPos();
	        }
	    },true);
        this.initResizeEvents(apzObj);
}, Apz.prototype.resizeTableHandler = function(trigger, e) {
    var timeout;
    var tblObj = $(trigger);
    var apzObj = this;
    var func = function(tblObj) {
      tblObj = $(tblObj);
      if (tblObj.is(":visible")) {
        var tableOldWidth = tblObj.attr("oldWidth");
        var tableCntWidth = tblObj.width();
        var tableWidth = tblObj.find("#" + trigger.id + "_table").outerWidth();
        var isResponsive = false;
        if (tblObj.find("#" + trigger.id + "_table").hasClass("responsive")) {
            isResponsive = true;
        }
        if (isResponsive) {
            if (tableOldWidth != tableCntWidth && Math.abs(parseInt(tableOldWidth) - parseInt(tableCntWidth)) > 10) {
                apzObj.startSpinner(tblObj.attr("id"));
                apzObj.initDataTable(tblObj.find('table[id].responsive:visible'), true);
                apzObj.closeResponsiveTableRows(tblObj.find('table[id].responsive:visible'));
                apzObj.stopSpinner(tblObj.attr("id"));
            }
        } else {
            if (tableOldWidth != tableCntWidth) {
                apzObj.startSpinner(tblObj.attr("id"));
                apzObj.initFixedHeaderTable(tblObj.find('table[id].fixedheader:visible'),true);
                apzObj.stopSpinner(tblObj.attr("id"));
            }
        }
      }
    }
    var threshold = 200;
    if (tblObj.find('table[id].fixedheader')) {
      threshold = 0;
    }
    return this.debounce(func,threshold);
}, Apz.prototype.debounce = function(func, wait, immediate) {
    var timeout;
    return function debounced () {
      var obj = this, args = arguments;
      function delayed () {
        timeout = null;
        if (!immediate) func.apply(obj, args);
      };
      var callNow = immediate && !timeout;
      if (timeout) {
        clearTimeout(timeout);
      }
      timeout = setTimeout(delayed, wait);
      if (callNow) func.apply(obj, args);
    };
}, Apz.prototype.initResizeEvents = function (apzObj) {
		var attachEvent = document.attachEvent;
  		var isIE = navigator.userAgent.match(/Trident/);
		  var requestFrame = (function(){
		    var raf = window.requestAnimationFrame || window.mozRequestAnimationFrame || window.webkitRequestAnimationFrame ||
		        function(fn){ return window.setTimeout(fn, 20); };
		    return function(fn){ return raf(fn); };
		  })();
		  
		  var cancelFrame = (function(){
		    var cancel = window.cancelAnimationFrame || window.mozCancelAnimationFrame || window.webkitCancelAnimationFrame ||
		           window.clearTimeout;
		    return function(id){ return cancel(id); };
		  })();
		  
		  function resizeListener(e){
		    var win = e.target || e.srcElement;
		    if (win.__resizeRAF__) cancelFrame(win.__resizeRAF__);
		    win.__resizeRAF__ = requestFrame(function(){
		      var trigger = win.__resizeTrigger__;
		      trigger.__resizeListeners__.forEach(function(fn){
		        fn.call(apzObj,trigger, e);
		      });
		    });
		  }
		  
		  function objectLoad(e){
		    this.contentDocument.defaultView.__resizeTrigger__ = this.__resizeElement__;
		    this.contentDocument.defaultView.addEventListener('resize', resizeListener);
		  }
		  
		  Apz.prototype.addResizeListener = function(element, fn){
		    if (!element.__resizeListeners__) {
		      element.__resizeListeners__ = [];
		      if (attachEvent) {
		        element.__resizeTrigger__ = element;
		        element.attachEvent('onresize', resizeListener);
		      }
		      else {
		        if (getComputedStyle(element).position == 'static') element.style.position = 'relative';
		        var obj = element.__resizeTrigger__ = document.createElement('object'); 
		        obj.setAttribute('style', 'display: block; position: absolute; top: 0; left: 0; height: 100%; width: 100%; overflow: hidden; pointer-events: none; z-index: -1;');
		        obj.__resizeElement__ = element;
		        obj.onload = objectLoad;
		        obj.type = 'text/html';
		        $(obj).attr('tabindex','-1');
		        if (isIE) element.appendChild(obj);
		        obj.data = 'about:blank';
		        if (!isIE) element.appendChild(obj);
		      }
		    }
		    element.__resizeListeners__.push(fn);
		  };
		  
		  Apz.prototype.removeResizeListener = function(element, fn){
		    element.__resizeListeners__.splice(element.__resizeListeners__.indexOf(fn), 1);
		    if (!element.__resizeListeners__.length) {
		      if (attachEvent) element.detachEvent('onresize', resizeListener);
		      else {
		        element.__resizeTrigger__.contentDocument.defaultView.removeEventListener('resize', resizeListener);
		        element.__resizeTrigger__ = !element.removeChild(element.__resizeTrigger__);
		      }
		    }
		  }

	}, Apz.prototype.initTableSearch = function(proc) {
		var $scr = $('#scr__'+proc.appId+'__'+ proc.scr + '__main');
		if ($scr.find('.srh').length>0) {
			$('.srh-icn').click(function(e){
				var currObj = $(this);
				var inputBox = $(this).siblings('span').find('.srh-inp');
				var srh = $(this).parent('.srh');
				if ($(srh).hasClass('srh-open')) {
					srh.removeClass('srh-open');
                    inputBox.focusout();
				} else {
					srh.addClass('srh-open');
                    inputBox.focus();
				}
            });
            $('.srh-inp').keydown(function(e){
                e.stopPropagation();
                var srh = $(this).parents('.srh:first');
                if (e.keyCode=="13" ||  e.keyCode=="9" ) {
                    srh.removeClass('srh-open');
                    $(this).focusout();
                } 
            });
		}
	}, Apz.prototype.initHorVerMenus = function(menuObj,dir,resp) {
		var menuId = menuObj.id;
		var $menu = $(menuObj);
		$menu.find("a[rel]").each(function(){
			var childMenu = $(this).parent('li').next('ul');
			$(this).parent('li').next('ul').remove();
			if (!$menu.find('.allchildmenus').length>0) {
				var divElm = document.createElement('div');
				$(divElm).attr('class','allchildmenus');
				$menu.append(divElm);
			}
			$(this).parents("#"+menuId+":first").find('.allchildmenus').append(childMenu);
		});
		ddlevelsmenu.menuclone[menuId] = "";
		ddlevelsmenu.topitems[menuId] = "";
		ddlevelsmenu.init(menuId, dir, resp);
		ddlevelsmenu.mql.addListener(function(){
			ddlevelsmenu.init(menuId, dir, ddlevelsmenu.mql.matches)
		})
	}, Apz.prototype.handleContextMenu = function(id, event) {
		if (id != ""){
			if (!this.isNull($("#" + id)[0])) {
				var sldrHeight = 0;
				var elm = $("#" + id);
				if ($("#page-body").find(elm).length > 0) {
					parent = $("#page-body");
				} else if ($("#footer").find(elm).length > 0) {
					parent = $("#footer");
				} else if ($("#header").find(elm).length > 0) {
					parent = $("#header");
				}
				var hdrHeight = jQuery('#header').height();
				if (this.isNull(hdrHeight)) {
					hdrHeight = 0;
				}
				if ($("#sidebar").is(':visible')) {
					var sldrHeight = jQuery('#sidebar').width();
					if (this.isNull(sldrHeight)) {
						sldrHeight = 0;
					}
				}
				if (parent != undefined) {
					elm.css("top", "auto");
					elm.css("right", "auto");
					elm.css("bottom", "auto");
					elm.css("left", "auto");
					var parentHeight = parent.height();
					var parentWidth = parent.width();
					var posY = event.pageY - hdrHeight;
					var posX = event.pageX - sldrHeight;
					if (elm.outerHeight() > (parentHeight - posY)) {
						elm.css('bottom', (parentHeight - posY) + 'px');
					} else {
						elm.css('top', posY + "px");
					}
					if (elm.outerWidth() > (parentWidth - posX)) {
						elm.css('right', (parentWidth - posX) + 'px');
					} else {
						elm.css('left', posX + "px");
					}
				}
			}
		}
	}, Apz.prototype.handleHeader = function(event) {
		var divObj = document.getElementById("header");
		if (divObj) {
			var nodes = divObj.getElementsByTagName('*');
			for (var k = 0; k < nodes.length; k++) {
				tagType = nodes[k].tagName.toLowerCase();
				if (tagType == "div" || tagType == "ul" || tagType == "li") {
					event.stopPropagation();
				}
			}
		}
	}, Apz.prototype.handleCalendar = function(){
		this.dateformat = this.handleDateFormat();
		this.datetimeformat = this.handleDateTimeFormat();
		this.timeformat = this.handleTimeFormat();
	}, Apz.prototype.handleDateFormat = function() {
		var userfrmt = this.dateFormat;
		userfrmt = userfrmt.toLowerCase();
		if (userfrmt.indexOf("yyyy") > -1) {
			userfrmt = userfrmt.replace("yyyy", "yy");
		} else {
			userfrmt = userfrmt.replace("yy", "y");
		}
		userfrmt = userfrmt.replace("mmmm", "MM");
		userfrmt = userfrmt.replace("mmm", "M");
		if (userfrmt.indexOf("dddd") > -1) {
			userfrmt = userfrmt.replace("dddd", "DD");
		} else if (userfrmt.indexOf("ddd") > -1) {
			userfrmt = userfrmt.replace("ddd", "D");
		}
		return userfrmt;
	}, Apz.prototype.handleDateTimeFormat = function() {
		var userfrmt = this.dateTimeFormat;
		var str = [];
		var timeIndex = userfrmt.indexOf("h") > -1 ? userfrmt.indexOf("h")
				: userfrmt.indexOf("H");
		if (timeIndex > -1) {
			str[0] = userfrmt.substring(0, timeIndex).trim();
			str[1] = userfrmt.substring(timeIndex).trim();
		} else {
			str[0] = userfrmt.trim();
			str[1] = "";
		}
		str[0] = str[0].toLowerCase();
		if (str[0].indexOf("yyyy") > -1) {
			str[0] = str[0].replace("yyyy", "yy");
		} else {
			str[0] = str[0].replace("yy", "y");
		}
		str[0] = str[0].replace("mmmm", "MM");
		str[0] = str[0].replace("mmm", "M");
		str[1] = str[1].replace("mm", "ii");
		str[1] = str[1].replace("m", "i");
		str[1] = str[1].replace("tt", "A");
		userfrmt = str[0].concat("  ");
		userfrmt = userfrmt.concat(str[1]);
		userfrmt = userfrmt.replace("tt", "a");
		userfrmt = userfrmt.replace("t", "a");
		userfrmt = userfrmt.replace("TT", "A");
		userfrmt = userfrmt.replace("T", "A");
		if (userfrmt.indexOf("dddd") > -1) {
			userfrmt = userfrmt.replace("dddd", "DD");
		} else if (userfrmt.indexOf("ddd") > -1) {
			userfrmt = userfrmt.replace("ddd", "D");
		}
		return userfrmt.trim();
	}, Apz.prototype.handleTimeFormat = function() {
		var userfrmt = this.timeFormat;
		userfrmt = userfrmt.replace("mm", "ii");
		userfrmt = userfrmt.replace("m", "i");
		userfrmt = userfrmt.replace("tt", "A");
		return userfrmt;
	}, Apz.prototype.initControls = function(proc) {
		var noOfElms, elms;
		if(this.isNull(this.scrMetaData.uiInitsMap)){
			this.scrMetaData.uiInitsMap = [];
		}
		var inits = proc.uiInits;
		var scrName = this.childScr;
		//var allElms = this.scrMetaData.elms;

		if (inits != undefined) {
			// // Tags init
			elms = inits.tag;
			if (elms != undefined) {
				noOfElms = elms.length;
				for (var e = 0; e < noOfElms; e++) {
					var tagObj = elms[e];
					var uiObj =  document.getElementById(tagObj[0]);
					if(uiObj){
						var id = this.getObjIdWORowNumber(uiObj);
						this.scrMetaData.uiInitsMap[id] = tagObj;
						this.initTags(tagObj[0], tagObj[1]);
					}
				}
			}
			// // Dates init
			elms = inits.date;
			if (elms != undefined) {
				noOfElms = elms.length;
				for (var e = 0; e < noOfElms; e++) {
					var dateObj = elms[e];
					var uiObj =  document.getElementById(dateObj[0]);
					if(uiObj){
					var params = {};
					params.id = dateObj[0];
					params.dataType = dateObj[1];
					params.lookAndFeel = dateObj[2];
					params.parentDisplay = dateObj[3];
					params.style = dateObj[4];
					params.parentPreset = dateObj[5];
					params.parentMinDate = dateObj[6];
					params.parentMaxDate = dateObj[7];
					params.closeOnSel = dateObj[8];
					params.multiSel = dateObj[9];
					params.parentStartYear = dateObj[10];
					params.parentEndYear = dateObj[11];
					params.parentRangePick = dateObj[12];
					params.secInputId = dateObj[13];
					params.parentMultiInput = dateObj[14];
                    params.dateType = dateObj[15];
					this.initDates(params);
					var id = this.getObjIdWORowNumber(uiObj);
					this.scrMetaData.uiInitsMap[id] = dateObj;
					}
				}
			}
			// // Dropdowns init
			elms = inits.dropDown;
			if (elms != undefined) {
				noOfElms = elms.length;
				for (var e = 0; e < noOfElms; e++) {
					var dropdownObj = elms[e];
					var uiObj =  document.getElementById(dropdownObj[0]);
					if(uiObj){
						this.initDropdowns(dropdownObj);
						var id = this.getObjIdWORowNumber(uiObj);
						this.scrMetaData.uiInitsMap[id] = dropdownObj;
					}
				}
			}
			// // Checkbox init
			elms = inits.checkBox;
			if (elms != undefined) {
				noOfElms = elms.length;
				for (var e = 0; e < noOfElms; e++) {
					var checkboxObj = elms[e];
					var uiObj =  document.getElementById(checkboxObj[0]);
					if(uiObj){
						this.initCheckboxs(checkboxObj[0], checkboxObj[1]);
						var id = this.getObjIdWORowNumber(uiObj);
						this.scrMetaData.uiInitsMap[id] = checkboxObj;
					}
				}
			}	

			// context menu init
			elms = inits.contextMenu;
			if (elms != undefined) {
				noOfElms = elms.length;
				for (var e = 0; e < noOfElms; e++) {
					var contextmenuObj = elms[e];
					var uiObj =  document.getElementById(contextmenuObj[0]);
					if(uiObj){
						this.initContextMenu(contextmenuObj[0], contextmenuObj[1]);
						var id = this.getObjIdWORowNumber(uiObj);
						this.scrMetaData.uiInitsMap[id] = contextmenuObj;
					}
				}
			}
		
			/// dropdown with input init
			elms = inits.dropdownWithInput;
			if (elms != undefined) {
				noOfElms = elms.length;
				for (var e = 0; e < noOfElms; e++) {
					var dropdownObj = elms[e];
                    var uiObj =  document.getElementById(dropdownObj[0]);
					if (uiObj && $("#"+dropdownObj[0]+"_ext").find('.dropdown-list').length != 0) {
						var dropdownListId = $("#"+dropdownObj[0]+"_ext").find('.dropdown-list')[0].id;
						this.initDropdownWithInput(dropdownObj[0], dropdownListId);
						var id = this.getObjIdWORowNumber($("#"+dropdownObj[0])[0]);
						this.scrMetaData.uiInitsMap[id] = dropdownObj;
					} else {
						this.initNativeDropdownWithInput(dropdownObj[0]);
						var id = this.getObjIdWORowNumber($("#"+dropdownObj[0])[0]);
						this.scrMetaData.uiInitsMap[id] = dropdownObj;
					}
				}
			}

			////popover init
			elms = inits.popover;
			if (elms != undefined) {
				noOfElms = elms.length;
				for (var e = 0; e < noOfElms; e++) {
					var popoverObj = elms[e];
					var uiObj =  document.getElementById(popoverObj[0]);
					if(uiObj){
						var params = {};
						params.elmId = popoverObj[0];
						params.targetId = popoverObj[1];
						params.position = popoverObj[3];
						this.initPopover(params);
						var id = this.getObjIdWORowNumber(uiObj);
						if (this.isNull(this.scrMetaData.uiInitsMap["popover"])) {
							this.scrMetaData.uiInitsMap["popover"] = [];
						}
						this.scrMetaData.uiInitsMap["popover"][id] = popoverObj;
						if (this.isNull(this.scrMetaData.elmsMap[id].popoverid)) {
							this.scrMetaData.elmsMap[id].popoverid = popoverObj[1];
						}
					}
				}
			}
		}
	}, Apz.prototype.initPopover = function(params) {
		/// Expects targetId, position, trigger
		var apzObj = this;
		var popoverCls = $('#'+params.targetId).attr("class");
		var obj = $("#"+params.targetId)[0];
		var triggerEvent = "click";
		if (!this.isNull(params.trigger)) {
			triggerEvent = params.trigger;
		}
		var position = params.position.toLowerCase();
        $("#"+params.elmId).popover({
    		html: true,
    		placement:position,
		    content: obj,
		    trigger: triggerEvent
		});
		$("#"+params.elmId).on("show.bs.popover", function(e){ 
			var popOverObj = $(this).data("bs.popover").tip();
			popOverObj.addClass(popoverCls);
			popOverObj.removeClass("sno");
			$("#" + params.targetId).removeClass();
			apzObj.initDataTables($('#'+params.targetId+' .responsive:visible'));
			apzObj.initFixedHeaderTables($('#'+params.targetId+' [id].fixedheader:visible'));
			if(apzObj.isFunction(apzObj.app.onPopoverOpen)){
				apzObj.app.onPopoverOpen(this,params.targetId,e);
			}
		});
		$("#"+params.elmId).on("hide.bs.popover", function(e){
			var popOverObj = $(this).data("bs.popover").tip();
			if(apzObj.isFunction(apzObj.app.onPopoverOpen)){
				apzObj.app.onPopoverOpen(this,params.targetId,e);
			}
		});
	}, Apz.prototype.initDropdowns = function(dropdownObj) {
		var obj = $("#" +dropdownObj[0])[0];
    var id = this.getObjIdWORowNumber(obj);
    var containerId = this.scrMetaData.elmsMap[id].container;
    var cntrData = this.scrMetaData.containersMap[containerId];
    if(!(cntrData.type == "FORM" && cntrData.readOnly == "Y")){
      this.initDropDownObj(dropdownObj,obj);
    }
	}, Apz.prototype.initDropDownObj = function(dropdownObj,obj) {
		var variation = dropdownObj[6];
		if (variation == "" || variation == "SIMPLE"){
			this.dropdownApz(obj);
		} else if (variation == "WITHSUBOPTIONS"){
			this.dropdownMultitiered(obj);
		} else if (variation == "MULTISELECTCHECKBOX"){
			this.dropdownCheckbox(obj);
		} else if (variation == "AUTOCOMPLETE" || variation == "MULTISELECTAUTOCOMPLETE"){
			this.dropdownAutocomplete(obj);
		}else if (variation == "MULTISELECTTAGS"){
			this.dropdownTags(obj);
		}
	}, Apz.prototype.initContextPopUp = function(elmId, targetId, position) {
		
	}, Apz.prototype.initContextMenu = function(contextId, targetId) {
		var doubleClicked = false;
		var apzObj = this;
		if (contextId != "" && $("#"+targetId).parent().hasClass('etb-ctmu')) {
			$("#" +contextId).on("contextmenu", function (e) {
				if($("#"+targetId).hasClass("context-open")){
					doubleClicked = true;
				} else {
					doubleClicked = false;
				}
				if(doubleClicked == false) {
				    e.preventDefault(); // To prevent the default context menu.
			        var mousePos = {};
				    var menuPos = {};
				    var menuDim = {};
				    menuDim.x = $("#" + targetId).width();
				    menuDim.y = $("#" + targetId).height();
				    mousePos.x = e.pageX;
				    mousePos.y = e.pageY;

				    if (mousePos.x + menuDim.x > $(window).width() + $(window).scrollLeft()) {
				        menuPos.x = mousePos.x - menuDim.x;
				    } else {
				        menuPos.x = mousePos.x;
				    }

				    if (mousePos.y + menuDim.y > $(window).height() + $(window).scrollTop()) {
				        menuPos.y = mousePos.y - menuDim.y;
				    } else {
				        menuPos.y = mousePos.y;
				    }
				    $("#" + targetId).css("left", menuPos.x);
				    $("#" + targetId).css("top", menuPos.y);
				    $("#" + targetId).fadeIn(500, apzObj.focusContextOut(contextId, targetId));
				    $("#"+targetId).addClass("context-open");
			    } else {
			      e.preventDefault();
			      $("#" + targetId).fadeOut(500);
			      $("#"+targetId).removeClass("context-open");
			    }
			    $('.context-open').not($("#"+targetId)).each(function(){
					$(this).removeClass('context-open');
			    });
			});
		}
	}, Apz.prototype.focusContextOut = function(contextId, targetId) {
		$('#' + contextId).on("click.apzevent", function () {
	      $("#" + targetId).fadeOut(500);
	      $('#' + contextId).off("click.apzevent");
	      $("#"+targetId).removeClass("context-open");           
	    });
	}, Apz.prototype.initTags = function(id, parentPlaceholder) {
		var tagName = document.getElementById(id).tagName;
		if (id != "" && tagName != "DD") {
			var existingdiv = $("#" + id).next();
			var objPlaceholder = "add a tag";
			if (!this.isNull(parentPlaceholder)) {
				objPlaceholder = parentPlaceholder;
			}
			var interact = true;
			$("#" + id).attr('apztype', 'tags');
			if(!this.isNull($('#'+id).attr('readonly')) || !this.isNull($('#'+id).attr('disabled'))){
				interact = false;
			}
			var obj = {
				'defaultText' : objPlaceholder,
				'interactive' : interact
				/*onChange : function() {
					apz.data.tagsDataInit(id);
				}*/
			};
			if (!existingdiv.hasClass("tagsinput")) {
				$("#" + id).tagsInput(obj);
			} else {
				existingdiv.remove();
				$("#" + id).tagsInput(obj);
			}
			tagClass = $("#" + id).attr('class');
			$("#" + id + "_tag").addClass("tagsinput "+tagClass);
			tagTitle = $("#" + id).attr('title');
			$("#" + id + "_tag").attr('title', tagTitle);
		}
	}, Apz.prototype.initMobiscrollDropdowns = function(id, autoComp, style, parentDisplay,closeOnSel, multisel, parentGroup) {
		if (id != "") {
			var existingelem = $("#" + id).prev();
			if (existingelem.prop("tagName") == "INPUT") {
				existingelem.remove();
			}
			if (autoComp == "Y") {
				var params = {};
				params.id = id;
				params.style = style;
				params.parentDisplay = display;
				this.autocomplete(params);
			} else {
				var obj = {
					theme : style.toLowerCase(),
					display : parentDisplay.toLowerCase(),
					mode : parentMode.toLowerCase(),
					closeOnSelect : closeOnSel == "Y" ? true : false,
					multiSelect : multisel == "Y" ? true : false,
					group : parentGroup == "Y" ? true : false,
				}
				$("#" + id).mobiscroll().select(obj);
			}
			var atrribStatus = $("#" + id).attr("disabled");
			if (typeof atrribStatus !== typeof undefined && atrribStatus !== false) {
				if ($("#" + id + "_" + "dummy").length) {
					$("#" + id + "_" + "dummy").attr('disabled', 'disabled');
				}
			} else {
				if ($("#" + id + "_" + "dummy").length) {
					$("#" + id + "_" + "dummy").removeAttr('disabled', 'disabled');
					$("#" + id + "_" + "dummy").removeAttr('enabled', 'enabled');
				}
			}
		}
	}, Apz.prototype.autocomplete = function(params) {
		//Expects id, style, parentDisplay
		var debounce, optionscontent, filtered, options = [], // // read options
		// from ui dropdown
		query = '';
		if (params.id != "") {
			allOptions = $("#" + params.id).children();
			for (i = 0; i < allOptions.length; i++) {
				options[i] = {
					description : allOptions[i].innerHTML,
					value : allOptions[i].value
				};
			}
			optionscontent = $.map(options, function(val, i) {
				return {
					text : '<div class="selectoption" data-code="' + val.value
							+ '"><div class="selectoption-name">' + val.description
							+ '</div></div>',
					value : val.value
				};
			});
			filtered = optionscontent;
			$("#" + params.id)
				.mobiscroll()
				.select(
					{
						theme : params.style.toLowerCase(),
						data : filtered,
						display : params.parentDisplay.toLowerCase(),
						layout : 'fixed',
						showLabel : false,
						height : 50,
						onMarkupReady : function(markup, inst) {
							markup.addClass('selectoptions');
							$(
								'<div style="padding:.5em"><input id="'+ params.id+ '_filter" class="md-filter-input" tabindex="0"'
									+ 'placeholder="Type to filter..."/></div>').prependTo($('.dwcc', markup))
									.on('keydown', function(e) {e.stopPropagation();})
									.on('keyup',function(e) {
										var that = $('input', this);
										clearTimeout(debounce);
										debounce = setTimeout(function() {
											query = that.val();
											filtered = $.grep(optionscontent,function(val) {
												return (val.text+ ', ' + val.value).match(new RegExp(query,'ig'));
											});
											inst.settings.data = filtered.length ? filtered : [ {
												text : 'No results',
												value : ''
											} ];
											inst.settings.parseValue(inst.settings.data[0].value);
											inst.refresh();
										}, 500);
									});
								},
						onBeforeShow : function(inst) {
							inst.settings.data = optionscontent;
							inst.refresh();
						},
						onSelect : function(v, inst) {
							$("#" + params.id + '_dummy').val(
								$(v).find('.selectoption-name').text());
						},
						onValueTap : function(item, inst) {
							var text = item.find('.selectoption-name').text();
							$("#" + id + '_filter').val(text);
						},
						onInit : function() {
							var v = $("#" + params.id + '_dummy').val();
							$("#" + params.id + '_dummy').val(
								$(v).find('.selectoption-name').text());
						}
					});
		}
	}, Apz.prototype.initCheckboxs = function(id, indeterminval) {
		if (id != "") {
			$("#" + id).prop("indeterminate", true);
		}
	}, Apz.prototype.initDates = function(params) {
		var obj = $("#"+params.id)[0];
		var actId = this.getObjIdWORowNumber(obj);
		if (this.isNull(this.scrMetaData.elmsMap[actId].dataType)) {
			this.scrMetaData.elmsMap[actId].dataType = params.dataType;
		}
		if(params.dateType == "JQUERY") {
			this.initJqueryDates(params);
		} else if (params.dateType != "OSSPECIFIC") {
			this.initAppzillonDates(params);
		}
	}, Apz.prototype.initAppzillonDates = function(params) {
		var lcontrols = new Array();
		var objDateFormat = this.dateformat;
		var objTimeFormat = this.timeformat;
		var initid = params.id;
		var currDateObj = document.getElementById(initid);
		var $el = $(currDateObj);
		if (params.dataType == "DATETIME") {
			var datetimeform = this.datetimeformat;
			var timeformstart = datetimeform.indexOf("h") > -1 ? datetimeform
					.indexOf("h") : datetimeform.indexOf("H");
			if (timeformstart != -1) {
				objDateFormat = datetimeform.substr(0, (timeformstart - 1));
				objTimeFormat = datetimeform.substr(timeformstart);
			} else {
				objDateFormat = datetimeform;
				objTimeFormat = "";
			}
		}
		var present = new Date();
		var previous = new Date((present.getFullYear() - 1), present.getMonth(),
				present.getDate());
		var next = new Date((present.getFullYear() + 1), present.getMonth(),
				present.getDate());
		var objMinDate = undefined;
		if (params.parentMinDate == "TODAY") {
			objMinDate = present;
		} else if (params.parentMinDate != "") {
			objMinDate = Date.parseExact(params.parentMinDate,this.dateFormat);
		}
		var objMaxDate = undefined;
		if (params.parentMaxDate == "TODAY") {
			objMaxDate = present;
		} else if (params.parentMaxDate != "") {
			objMaxDate = Date.parseExact(params.parentMaxDate,this.dateFormat);
		}
		var objPreSet = params.parentPreset;
		var obj = {
			theme : params.style.toLowerCase(),
			display : params.parentDisplay.toLowerCase(),
			setOnDayTap : params.closeOnSel == "Y" ? true : false,
			min : objMinDate,
			max : objMaxDate,
			multiSelect : params.multiSel == "Y" ? true : false,
			dateFormat : objDateFormat,
			timeFormat : objTimeFormat,
			showOnFocus : true,
			showOnTap : true
		}
		if (params.parentStartYear != "") {
			obj.startYear = params.parentStartYear;
		}
		if (params.parentEndYear != "") {
			obj.endYear = params.parentEndYear;
		}
		var readonly = false;
		var disabled = false;
		var secinputreadonly = false;
		var secinputdisabled = false;
		var secondObj = "";
		var secObjId = params.secInputId;
		if (secObjId != "") {
			if ($el.attr('rowno')) {
				secObjId = secObjId + "_" + $el.attr('rowno');
				secondObj = document.getElementById(secObjId);
			} else {
				secondObj = document.getElementById(secObjId);
			}
		}
		if (initid != "") {
			if (this.ranges.indexOf(initid) < 0) {
				readonly = $el[0].readOnly;
				disabled = $el[0].disabled;
				if ((params.dataType == "DATE" || params.dataType == "DATETIME")
						&& (params.parentPreset == "CALENDAR" || params.parentPreset == "DATE"
								|| params.parentPreset == "CALENDAR_DATE"
								|| params.parentPreset == "CALENDAR_DATETIME" || params.parentPreset == "CALENDAR_TIME")) {
					if (params.parentPreset == "CALENDAR") {
						lcontrols = [ 'calendar' ];
					} else if (params.parentPreset == "DATE") {
						lcontrols = [ 'date' ];
					} else if (params.parentPreset == "CALENDAR_DATE") {
						lcontrols = [ 'calendar', 'date' ];
					} else if (params.parentPreset == "CALENDAR_DATETIME") {
						lcontrols = [ 'calendar', 'date', 'time' ];
					} else if (params.parentPreset == "CALENDAR_TIME") {
						lcontrols = [ 'calendar', 'time' ];
					}
					objPreSet = "calendar";
					if (params.parentRangePick == "Y") {
						if (params.parentMultiInput == "Y" && secondObj != undefined) {
							if ($.mobiscroll.instances[secObjId] != undefined) {
								$(secondObj).mobiscroll("destroy");
							}
							secinputreadonly = secondObj.readOnly;
							secinputdisabled = secondObj.disabled;
							$el.mobiscroll().range($.extend(obj, {
								preset : objPreSet,
								endInput : "#" + secObjId,
								controls : lcontrols
							})).off('.dw').prop('readonly', false);
							$(secondObj).off('.dw').prop('readonly', false);
							this.ranges.push(secObjId);
							this.rangesmap[secObjId] = params.id;
						} else {
							$el.mobiscroll().range($.extend(obj, {
								preset : objPreSet,
								controls : lcontrols
							})).off('.dw').prop('readonly', false);
						}
					} else {
						$el.mobiscroll($.extend(obj, {
							preset : objPreSet,
							controls : lcontrols
						})).off('.dw').prop('readonly', false);
					}
				} else if (params.dataType == "DATETIME" && params.parentPreset == "DATETIME") {
					if (params.parentRangePick == "Y") {
						if (params.parentMultiInput == "Y" && secondObj != undefined) {
							if ($.mobiscroll.instances[secObjId] != undefined) {
								$(secondObj).mobiscroll("destroy");
							}
							secinputreadonly = secondObj.readOnly;
							secinputdisabled = secondObj.disabled;
							$el.mobiscroll().range($.extend(obj, {
								endInput : "#" + secObjId,
								controls : [ 'date', 'time' ],
							})).off('.dw').prop('readonly', false);
							$(secondObj).off('.dw').prop('readonly', false);
							this.ranges.push(secObjId);
							this.rangesmap[secObjId] = params.id;
						} else {
							$el.mobiscroll().range($.extend(obj, {
								controls : [ 'date', 'time' ],
							})).off('.dw').prop('readonly', false);
						}
					} else {
						$el.mobiscroll().datetime(obj).off('.dw').prop('readonly',
								false);
					}
				} else if (params.dataType == "TIME") {
					if (params.parentRangePick == "Y") {
						if (params.parentMultiInput == "Y" && secondObj != undefined) {
							if ($.mobiscroll.instances[secObjId] != undefined) {
								$(secondObj).mobiscroll("destroy");
							}
							secinputreadonly = secondObj.readOnly;
							secinputdisabled = secondObj.disabled;
							$el.mobiscroll().range({
								theme : params.style.toLowerCase(),
								endInput : "#" + secObjId,
								display : params.parentDisplay.toLowerCase(),
								controls : [ 'time' ],
								timeFormat : objTimeFormat,
								mode : parentMode.toLowerCase()
							}).prop('readonly', false);
							$(secondObj).prop('readonly', false);
							this.ranges.push(secObjId);
							this.rangesmap[secObjId] = params.id;
						} else {
							$el.mobiscroll().range({
								theme : params.style.toLowerCase(),
								display : params.parentDisplay.toLowerCase(),
								controls : [ 'time' ],
								timeFormat : objTimeFormat,
								mode : parentMode.toLowerCase()
							}).prop('readonly', false);
						}
					} else {
						$el.mobiscroll().time({
							theme : params.style.toLowerCase(),
							display : params.parentDisplay.toLowerCase(),
							timeFormat : objTimeFormat,
							mode : parentMode.toLowerCase()
						}).prop('readonly', false);
					}
				} else {
					if (params.parentRangePick == "Y") {
						if (params.parentMultiInput == "Y" && secondObj != undefined) {
							if ($.mobiscroll.instances[secObjId] != undefined) {
								$(secondObj).mobiscroll("destroy");
							}
							secinputreadonly = secondObj.readOnly;
							secinputdisabled = secondObj.disabled;
							$el.mobiscroll().range($.extend(obj, {
								endInput : "#" + secObjId,
								preset : params.parentPreset
							})).off('.dw').prop('readonly', false);
							$(secondObj).off('.dw').prop('readonly', false);
							this.ranges.push(secObjId);
							this.rangesmap[secObjId] = params.id;
						} else {
							$el.mobiscroll().range($.extend(obj, {
								preset : params.parentPreset,
							})).off('.dw').prop('readonly', false);
						}
					} else {
						$el.mobiscroll($.extend(obj, {
							preset : params.parentPreset
						})).off('.dw').prop('readonly', false);
					}
				}
				if (readonly) {
					$el[0].readOnly = true;
				} else if (disabled) {
					$el[0].disabled = true;
				}
				if (secinputreadonly) {
					secondObj.readOnly = true;
				} else if (secinputdisabled) {
					secondObj.disabled = true;
				}
			}
		}
		//}
	}, Apz.prototype.initJqueryDates = function(param) {
    var params =  {dateFormat: this.dateformat,
    firstDay: 1,
    //dayNamesMin: [ "Sun", "Mon", "Tues", "Wed", "Thurs", "Fri", "Sat" ],
    showOtherMonths: true,
    selectOtherMonths: true,
    minDate: Date.parseExact(param.parentMinDate,this.dateFormat),
    maxDate: Date.parseExact(param.parentMaxDate,this.dateFormat),
    changeMonth: true,
    changeYear: true
    };
    if(param.secInputId!=="" && $("#"+param.id).attr("firstinputid")==undefined){
     document.getElementById(param.id).setAttribute("secondinputid",param.secInputId);
     document.getElementById(param.secInputId).setAttribute("firstinputid",param.id);
    }
      if(param.parentRangePick=="Y" && param.parentMultiInput=="N"){
        var previousDate = "",fromDate= "",toDate="",dateSelected="";
        params.beforeShow = function(inputDate){
          previousDate = this.value.split(" ");
          fromDate = new Date(previousDate[0]);
          toDate = new Date(previousDate[2]);
        }
        params.beforeShowDay = function(date1){
          if(dateSelected==""){ 
            if(date1>=fromDate&&date1<=toDate){
              return [true,'actdate'];
            }
          }
          return [true,'' ];
        }
      params.onSelect = function(selectedDate) {
        dateSelected = selectedDate;
         if(!$(this).data().datepicker.first){
              $(this).data().datepicker.inline = true
              $(this).data().datepicker.first = selectedDate;
          }else{
              if(selectedDate > $(this).data().datepicker.first){
                  $(this).val($(this).data().datepicker.first+" - "+selectedDate);
              }else{
                  $(this).val(selectedDate+" - "+$(this).data().datepicker.first);
              }
              $(this).data().datepicker.inline = false;
          }
            $(".activeDates").removeClass("activeDates");
      }
       params.onClose = function(){
       delete $(this).data().datepicker.first;
          $(this).data().datepicker.inline = false;
          dateSelected = "";
      }
      }else if(param.eventCalender == "Y"){
        params.beforeShowDay = enableDays;
      }else if(param.parentRangePick=="Y" &&(param.parentMultiInput=="Y" &&($("#"+param.id).attr("firstinputid")!==undefined||$("#"+param.id).attr("secondinputid")!==undefined))){
        var dateSelected ="";
        params.onSelect = function(selectedDate){
          dateSelected = selectedDate;
        }
        params.onClose = function(){
          if(document.getElementById(param.id).hasAttribute("secondinputid")){
            $( "#"+param.secInputId ).datepicker( "option", "minDate", dateSelected );
          }else{
            var firstId = document.getElementById(param.id).getAttribute("firstinputid")
            $( "#"+firstId ).datepicker( "option", "maxDate", dateSelected );
          }
        }
    }
      if($("#"+param.id).hasClass("hasDatepicker")){
        $("#"+param.id).removeClass("hasDatepicker");
      }
      $("#"+param.id).datepicker(params);
	}, Apz.prototype.showCalendar = function(elmId, dataType, rangePicker, multiInput,secInputId) {
		if($("#"+elmId).hasClass('hasDatepicker')) {
			$("#"+elmId).trigger('focus');
		} else {
			if (elmId != "") {
			if (($("#" + elmId).attr("disabled") == undefined)
				&& ($("#" + elmId).attr("readonly") == undefined)) {
				var objVal = $("#" + elmId).val();
				var origId = elmId;
				var objFunction = "setDate", functionParam = objVal;
				var secondIdVal = "", ranges, objFormat=this.dateformat;
			  if(dataType == "DATETIME") {
					objFormat = this.datetimeformat;
				} else if (dataType == "TIME") {
					objFormat = this.timeformat;
				}
				if (rangePicker == "Y") {
					var date1, date2;
					objFunction = "setValue";
					if (multiInput == "Y" && secInputId != "") {
						secondIdVal = $("#" + secInputId).val();
						date1 = mobiscroll.util.datetime.parseDate(objFormat, objVal);
						date2 = mobiscroll.util.datetime.parseDate(objFormat, secondIdVal);
						if (date1 != null && date2 != null) {
							functionParam = [ date1, date2 ];
						} else {
							functionParam = objVal == "" ? new Date() : mobiscroll.util.datetime.parseDate(
						objFormat, objVal);
						}
					} else {
						ranges = objVal.split(" - ");
						if (ranges.length > 1) {
							date1 = mobiscroll.util.datetime.parseDate(objFormat, ranges[0]);
							date2 = mobiscroll.util.datetime.parseDate(objFormat, ranges[1]);
							functionParam = [ date1, date2 ];
						} else {
							functionParam = objVal == "" ? new Date() : mobiscroll.util.datetime.parseDate(
						objFormat, objVal);
						}
					}
				} else if (this.ranges.indexOf(elmId) >= 0) {
					var date1, date2;
					objFunction = "setValue";
					var lfirstdateid = this.rangesmap[origId];
					elmId = lfirstdateid;
					if (lfirstdateid) {
						lfirstdateval = $("#" + lfirstdateid).val();
					}
					date1 = mobiscroll.util.datetime.parseDate(objFormat, lfirstdateval);
					date2 = mobiscroll.util.datetime.parseDate(objFormat, objVal);
					if (date1 != null && date2 != null) {
						functionParam = [ date1, date2 ];
					}
				} else {
					functionParam = objVal == "" ? new Date() : mobiscroll.util.datetime.parseDate(
						objFormat, objVal);
				}
				if (functionParam != "") {
					$("#" + elmId).mobiscroll(objFunction, functionParam);
					$("#" + elmId).mobiscroll('show');
				}
			}
		}
		}
	}, Apz.prototype.initRow = function(tableId, pobj) {    // // On Row Click initilize Mobiscroll Controls
		var containerData = this.scrMetaData.containersMap[tableId];
		var noOfElms = containerData.elms.length;
		var noOfRows = containerData.pageRows;
		for (var c = 0; c < noOfElms; c++) {
			var currElm = containerData.elms[c];
			var currRecHtmlId = currElm.id + "_" + (noOfRows - 1);
			var mainRowObj = this.scrMetaData.uiInitsMap[currElm.id];
			var popoverId = this.scrMetaData.elmsMap[currElm.id].popoverid;
			if (currElm.type == "INPUTBOX" || currElm.type == "INPUTWITHBUTTON") {
				if (currElm.dataType == "DATE" || currElm.dataType == "DATETIME" || currElm.dataType == "TIME") {
					var params = {};
					params.id = currRecHtmlId;
					params.dataType = mainRowObj[1]
					params.lookAndFeel = mainRowObj[2]
					params.parentDisplay = mainRowObj[3]
					params.style = mainRowObj[4]
					params.parentPreset = mainRowObj[5]
					params.parentMinDate = mainRowObj[6]
					params.parentMaxDate = mainRowObj[7]
					params.closeOnSel = mainRowObj[8]
					params.multiSel = mainRowObj[9]
					params.parentStartYear = mainRowObj[10]
					params.parentEndYear = mainRowObj[11]
					params.parentRangePick = mainRowObj[12]
					params.secInputId = mainRowObj[13];
					params.parentMultiInput = mainRowObj[14];
                    params.dateType = mainRowObj[15];
					this.initDates(params);
				}
			} else if (currElm.type == "DROPDOWN") {
				mainRowObj[0] = currRecHtmlId;
				if (mainRowObj[6] == "AUTOCOMPLETE" || mainRowObj[6] == "MULTISELECTAUTOCOMPLETE") {
					$("#"+currRecHtmlId).siblings("span").remove();
				}
				this.initDropdowns(mainRowObj);
			} else if (currElm.type == "DROPDOWNWITHINPUT") {
				mainRowObj[0] = currRecHtmlId;
				if ($("#"+mainRowObj[0]+"_ext").find('.dropdown-list').length != 0) {
					var dropdownListId = $("#"+mainRowObj[0]+"_ext").find('.dropdown-list')[0].id;
					this.initDropdownWithInput(mainRowObj[0], dropdownListId);
				} else {
					this.initNativeDropdownWithInput(mainRowObj[0]);
				}
				this.initDropdowns(mainRowObj);
			} else if (currElm.type == "TAGS") {
				this.initTags(currRecHtmlId, mainRowObj[1]);
			}
			if (!this.isNull(popoverId)) {
				var params = {};
				var popoverObj = this.scrMetaData.uiInitsMap["popover"][currElm.id];
				params.elmId = currRecHtmlId;
				params.targetId = popoverObj[1];
				params.position = popoverObj[3];
				this.initPopover(params);
			}
		}
	}, Apz.prototype.handleCarousels = function(slides, index) {
		var tagType = "";
		var carDivLength = slides.length;
		for (var i = 0; i < carDivLength; i++) {
			var currDiv = slides[i];
			var lnodes = currDiv.getElementsByTagName('*');
			for (var k = 0; k < lnodes.length; k++) {
				tagType = lnodes[k].tagName.toLowerCase();
				if (tagType == "input" || tagType == "select"
						|| tagType == "textarea" || tagType == "span"
						|| tagType == "a") {
					if (index == i) {
						$(lnodes[k]).css("visibility", "visible");
					} else {
						$(lnodes[k]).css("visibility", "hidden");
					}
				}
			}
		}
	}, Apz.prototype.initSidebarState = function() {
		var $rolepage = $(".rolepage");
		var sideBar = document.getElementById('sidebar');
		$($rolepage).removeClass('apz-nav-push apz-nav-stay-left apz-nav-stay-right');
		if (!this.isNull(sideBar)){
			var $sideBar = $(sideBar);
			if ($sideBar.hasClass('apz-nav-open')) {
				if (($sideBar.attr('sidebartype')=='STATIC')) {
					if ($sideBar.hasClass('lft')) {
						$($rolepage).addClass('apz-nav-push apz-nav-stay-left');
					} else if ($sideBar.hasClass('rht')) {
						$($rolepage).addClass('apz-nav-push apz-nav-stay-right');
					}
				}
			}
		}
	}, Apz.prototype.toggleSidebar = function(obj) {
		if (!$("#sidebar").hasClass('apz-nav-open')){
			this.openSidebar();
		} else {
			this.closeSidebar();
		}
	}, Apz.prototype.openSidebar = function() {
		$("#sidebar").addClass('apz-nav-open');
		this.toggleSidebarRelatedContent();
		this.marginSetForSideBar();
	}, Apz.prototype.closeSidebar = function() {
		$("#backdrop").removeClass('in');
		$("#sidebar").removeClass('apz-nav-open');
		this.toggleSidebarRelatedContent();
        this.marginSetForSideBar();
	}, Apz.prototype.toggleSidebarRelatedContent = function() {
		var $sideBar = $("#sidebar");
		var $rolePage = $(".rolepage");
		var showBackdrop = document.getElementById('backdrop');
		if ($sideBar.attr('sidebartype')=='STATIC') {
			if ($sideBar.hasClass('lft')) {
				$rolePage.toggleClass('apz-nav-push apz-nav-stay-left');
			} else if ($sideBar.hasClass('rht')) {
				$rolePage.toggleClass('apz-nav-push apz-nav-stay-right');
			}
		}
		if (!$rolePage.hasClass('apz-nav-push') && $sideBar.hasClass('apz-nav-open')) {
			$("#backdrop").addClass('in');
		}
		// //Readjust Height..
		//this.readjustHeight();
	}, Apz.prototype.marginSetForSideBar = function (){
		var hdrobj = jQuery('.pnt-head');
		var hdrheight = hdrobj.outerHeight();
        var ftrobj = jQuery('.pnt-foot');
		var ftrheight = ftrobj.outerHeight();
		var lmenuwidth = jQuery('.pnt-sdbr.lft').outerWidth();
		 
        if (!hdrobj.hasClass("apz-nav-open")){
            jQuery(".pagebody").css("padding-top", 0); 
            jQuery(".pnt-sdbr.lft").css("top", 0); 
            jQuery(".pnt-sdbr.rht").css("top", 0); 
        }	
        else{
            jQuery(".pagebody").css("padding-top", hdrheight); 
            jQuery(".pnt-sdbr.lft").css("top", hdrheight); 
            jQuery(".pnt-sdbr.rht").css("top", hdrheight); 
        }
     
        if (!ftrobj.hasClass("apz-nav-open")){
            jQuery(".pagebody").css("padding-bottom", 0);
            jQuery(".pnt-sdbr.lft").css("bottom", 0);
            jQuery(".pnt-sdbr.rht").css("bottom", 0);
        }
        else{
            jQuery(".pagebody").css("padding-bottom", ftrheight);
            jQuery(".pnt-sdbr.lft").css("bottom", ftrheight);
            jQuery(".pnt-sdbr.rht").css("bottom", ftrheight);
            
        } 
    }, Apz.prototype.fixSidebar = function() {
		//// TBC
	}, Apz.prototype.unFixSidebar = function() {
		//// TBC
	}, Apz.prototype.handleStepperclick = function (pobj) { 
        var inpVal = $(pobj).parent().siblings().find('input').val();
        var step = $(pobj).parent().siblings().find('input').attr('step');
        var maxVal = $(pobj).parent().siblings().find('input').attr('max');
        var minVal = $(pobj).parent().siblings().find('input').attr('min');
        var minusObj = $(pobj).parent().parent().children('li:first').children('button');
        var plusObj = $(pobj).parent().parent().children('li:last').children('button');
        if (step == "" || step == undefined) {
        	step = 1;
        }
        var val = 0;
        if (!this.isNull(inpVal)) {
        	val = inpVal;
            if(!isNaN(inpVal) && $.isNumeric(inpVal)){
            	inpVal = parseFloat(inpVal);
                step = parseFloat(step);
                if($(pobj).hasClass('stpr-add')) {
                	if((inpVal+step) <=  maxVal ){
						//var minusObj = $(pobj).parent().siblings().find('button');
						//minusObj.removeAttr('disabled');
						/*if((inpVal+step) == maxVal) {
							$(pobj).attr('disabled','disabled');
						}*/
                		if((inpVal+step) < minVal){
                			val = minVal;
                		} else {
                			val= (inpVal+step); 
                		}
                	} /*else{
						$(pobj).attr('disabled','disabled');
					}*/
                } else {
                	if((inpVal-step) >= minVal){
						//var plusObj = $(pobj).parent().siblings().find('button');
						//plusObj.removeAttr('disabled');
						/*if(inpVal-step == minVal) {
							$(pobj).attr('disabled','disabled');
						}*/
                		if((inpVal - step) > maxVal){
                			val = maxVal;
                        } else {
                        	val = (inpVal-step);
                        }
                	} /*else{
						$(pobj).attr('disabled','disabled');
					}*/
                }
            } else{
            	alert('Please enter valid Number');                                                                         
            } 
        }else{                                                   
        	val = minVal;                                                     
        }
        if(val <= minVal) {
        	minusObj.attr('disabled','disabled');
        	plusObj.removeAttr('disabled');
        } else if(val >= maxVal) {
        	plusObj.attr('disabled','disabled');
        	minusObj.removeAttr('disabled');
        } else if (!isNaN(val)){
        	minusObj.removeAttr('disabled');
        	plusObj.removeAttr('disabled');
        }
        $(pobj).parent().siblings().find('input').val(val); 
	}, Apz.prototype.validateStepperVal = function (pobj) {
         var inputVal = parseFloat(pobj.value);
         var minVal = parseFloat(pobj.getAttribute("min"));
         var maxVal = parseFloat(pobj.getAttribute("max"));
         var minusObj = $(pobj).parent().siblings('li:first').children('button');
         var plusObj = $(pobj).parent().siblings('li:last').children('button');
         if(inputVal <= minVal) {
         	pobj.value = minVal;
         	minusObj.attr('disabled','disabled');
         	plusObj.removeAttr('disabled');
         } else if(inputVal >= maxVal) {
         	pobj.value = maxVal;
         	plusObj.attr('disabled','disabled');
         	minusObj.removeAttr('disabled');
         } else if(isNaN(inputVal)) {
         	alert('Please enter valid Number');
         } else {
         	plusObj.removeAttr('disabled');
         	minusObj.removeAttr('disabled');
         }
	}, Apz.prototype.initDropdownWithInput = function (dropdownId, dropdownListId) {
		//this.dropdownApz(document.getElementById(dropdownId));
		//this.initNativeDropdownWithInput(dropdownId, dropdownListId);
		var apzObj = this;
		var obj = $("#"+dropdownId)[0];
		$(obj).parents("#"+obj.id+"_ext:first").off('click.apzevent').on('click.apzevent', function(e) {
			if (!$(this).attr('disabled')) {
				apzObj.dropdownToggle(obj);
			}
		});
		$(obj).off('keyup.apzevent').on('keyup.apzevent', function(e) {
			if (!$(this).attr('disabled') && e.which=="13") {
				apzObj.dropdownToggle(obj);
			}
		});
		$("#"+dropdownListId).children().children().off('click.apzevent').on('click.apzevent',function(){
	       		if($(this).val() != "default"){
					$('#'+dropdownId).parents('#'+dropdownId+'_ext').parent().siblings('li').find('input').val($(this).text());
	       		}else{
	      			$('#'+dropdownId).parents('#'+dropdownId+'_ext').parent().siblings('li').find('input').text('');
	       		}
	       		$(this).siblings().removeClass('is-selected');
	       		$(this).addClass('is-selected');
    	});
	}, Apz.prototype.initNativeDropdownWithInput = function (dropdownId) {
		$("#"+dropdownId).on('change.apzevent',function() {
	       		if($(this).val() != "default"){
					$('#'+dropdownId).parent().siblings('li').children('input').val(this.options[this.selectedIndex].innerHTML);
	       		}else{
	      			$('#'+dropdownId).parent().siblings('li').children('input').val('');
	       		}
    	});

	}, Apz.prototype.sortAction = function(container, element, tblHdr) {
		var sortType = false;
		var hdrElm = $(tblHdr);
		var spanCntnt = hdrElm.text();
		var sblngHdr = hdrElm.siblings('th');
		var i=0;
		if (hdrElm.hasClass("sorting_asc")) {
			sortType = true;
			hdrElm.attr({'aria-label':spanCntnt+':activate to sort column ascending','aria-sort':'descending'});
			hdrElm.removeClass("sorting sorting_asc").addClass("sorting_desc");
		}else{
			hdrElm.attr({'aria-label':spanCntnt+':activate to sort column descending','aria-sort':'ascending'}); 
			hdrElm.removeClass("sorting sorting_desc").addClass("sorting_asc");     
		}
		//TBC - is the below if condition really required ??
		if($(sblngHdr[0]).children().find('input').hasClass('group-checkable')){
			i=1;
		}
		for(i;i<sblngHdr.length;i++){
			var repCnt = $(sblngHdr[i]).text();
			$(sblngHdr[i]).attr('aria-label',repCnt).removeAttr('aria-sort').removeClass("sorting_asc sorting_desc").addClass("sorting");
		}
		var params = {};
		params.container = container;
		params.element = element;
		params.sortType = sortType;
		this.sortRecords(params);
   }, Apz.prototype.dropdownAutocomplete = function(obj) {
   		$(obj).select2({
  			width: '100%'
		});
   		$(obj).on("select2:open", function() {    
			$(".select2-search--dropdown .select2-search__field").attr("placeholder", "");
		});
		$(obj).on("select2:close", function() {    
			$(obj).focus();
		});
	}, Apz.prototype.dropdownCheckbox = function(obj) {
    var drdnDivObj = $(obj).parents("#"+obj.id+"_ext:first");
		var apzObj = this;
		drdnDivObj.off('click.apzevent').on( 'click.apzevent', function() {
			if (!$(this).attr('disabled')) {
				apzObj.dropdownToggle(obj);
			}
		});
		$(obj).off('keydown.apzevent').on('keydown.apzevent', function(e) {
      if (!$(this).attr('disabled') && (e.which=="13" || e.which == "32")) {
        if(e.keyCode == 32){
          e.preventDefault();
        }
        apzObj.dropdownToggle(obj);
      }
    });
    var val = "";
    var setDropdownVal = function(curObj,event) {
      event.preventDefault();
      if(event.keyCode == 13 || event.type == "change") {
        if (event.keyCode == 13) {
          curObj = $(curObj).find('input');
          if ($(curObj).is(':checked')){
            $(curObj).prop('checked', false);
          } else {
            $(curObj).prop('checked', true);
          }
        }
        var txtbox = $(obj);
        var allVals = [];
        $(curObj).closest('.sub-ctr').find('li.is-selected').removeClass('is-selected');
        $(curObj).closest('.sub-ctr').find(':checked').each(function() {
          //$(this).parent('li').addClass('is-selected');
          allVals.push($(this).next().text());
        });
        var allValsString = allVals.join(', ');
        txtbox.val(allValsString);
        if ($(obj).attr("onchange")) {
          $(obj).trigger("change");
        }
        event.stopPropagation();
      } else {
        apzObj.dropdownKeyAction(obj,curObj,event);
      }
    }
    drdnDivObj.find('li input').off('change.apzevent').on('change.apzevent', function(e) {
      setDropdownVal(this,e);
    });
    var searchDrdn = function(curObj,event){
      var value = val;
      var proceed = true;
      var totalOpts = $(curObj).parent().children('li').length;
      var index = 0;
      var getNextOpt = function(actObj,currOpt,nextOptObj,curValue) {
        nextOpt = $(nextOptObj).next('li');
        if (nextOptObj.text().toUpperCase().indexOf(curValue) != -1 && $(nextOptObj).text().toUpperCase().indexOf(curValue) == '0') {
          $(nextOptObj).parent().find('.hilt').removeClass('hilt');
          nextOptObj.addClass('hilt').focus();
          proceed = false;
        } else if ($(nextOptObj).next('li').length == 0 && index<totalOpts) {
          nextOpt = $(nextOptObj).parent().children('li:first');
          index = index + 1;
          getNextOpt(actObj,nextOptObj,nextOpt,curValue);
        } else {
          if (index<totalOpts) {
            index = index + 1;
            getNextOpt(actObj,nextOptObj,nextOpt,curValue);
          }
        }
      }
      if (proceed) {
        var nextOpt = $(curObj).next('li');
        if (nextOpt.length == 0) {
          nextOpt = $(curObj).parent().children('li:first');
        }
        getNextOpt(obj,curObj,nextOpt,value);
      }
    }
    var clear = false;
    drdnDivObj.find('li:not(.is-disabled)').off('keydown.apzevent').on( 'keydown.apzevent', function(e) {
      if (/[a-zA-Z0-9]/.test(String.fromCharCode(e.keyCode))) {
        if (!(val.length == '1' && val == String.fromCharCode(e.keyCode))) {
          val = val + String.fromCharCode(e.keyCode);
        }
        clearTimeout($.data(obj, 'timer'));
        var wait = setTimeout(function() {
          clear = true;
          val = "";
        }, 500);
        $(obj).data('timer', wait);
        console.log($(this).children().text() + "   " + val);
        if ($(this).children().text().toUpperCase().indexOf(val)!=0 || clear) {
          clear = false;
          searchDrdn(this,e);
        }
      } else {
        setDropdownVal(this,e);
      }
    });
	}, Apz.prototype.dropdownMultitiered = function(obj) {
		var apzObj= this;
    var drdnDivObj = $(obj).parents("#"+obj.id+"_ext:first");
		drdnDivObj.off('click.apzevent').on( 'click.apzevent', function() {
      if (!$(this).attr('disabled')) {
        apzObj.dropdownToggle(obj);
      }
    });
    $(obj).off('keydown.apzevent').on('keydown.apzevent', function(e) {
      if (!$(this).attr('disabled') && (e.which=="13" || e.which == "32")) {
        if(e.keyCode == 32){
          e.preventDefault();
        }
        apzObj.dropdownToggle(obj);
      }
    });
    var val = "";
    var setDropdownVal = function(curObj,event) {
      event.preventDefault();
      if(event.keyCode == 13 || event.type == "click") {
        if (!$(curObj).hasClass("is-selected")){
          var newtext = $(curObj).text();
			//$(this).siblings().removeClass('is-selected');
			$(curObj).parents("div:first").find('.is-selected').removeClass('is-selected');
			$(curObj).addClass('is-selected');
			$(obj).val(newtext);
			if ($.validator) {
			  $(curObj).find('input').valid();
			}
	  		apzObj.dropdownToggle(obj);
			if ($(obj).attr("onchange")) {
				$(obj).trigger("change");
			}
			event.stopPropagation();
        } else {
          apzObj.dropdownToggle(obj);
        }
        if ($(".drdn-extspan").length==0) {
          $(obj).focus();
        }
      } else {
        if ((event.keyCode==39 && $(curObj).children('span').hasClass('is-expanded')) || (event.keyCode==37&& $(curObj).parent('ul') && $(curObj).parent('ul').prev('li').children('span').hasClass('is-expanded'))) {
          if (event.keyCode==39) {
            if ($(curObj).next('ul').length>0) {
              $(curObj).removeClass('hilt');
              $(curObj).next('ul').children('li:first').addClass('hilt').focus();
            }
          } else {
            if ($(curObj).parent('ul').length>0) {
              $(curObj).removeClass('hilt');
              $(curObj).parent('ul').prev('li').addClass('hilt').focus();
            }
          }
        } else {
          apzObj.dropdownKeyAction(obj,curObj,event);
        }
      }
    }
		drdnDivObj.find('li:not(.is-disabled)').off('click.apzevent').on( 'click.apzevent', function(e) {
      setDropdownVal(this,e);
		});
		drdnDivObj.find('.menu-expand').off('click.apzevent').on( 'click.apzevent', function(e) {
			$(this).children('svg').remove();
		    $(this).toggleClass('is-expanded').closest('li').next('ul').toggle();
        $(this).closest('.sub-ctr').find('.hilt').not('.is-selected').removeClass('hilt');
        $(this).parent('li').focus();
		    e.stopPropagation();
		});
		// Don't show hover background on multi-tiered plus icon
		drdnDivObj.find('li:not( .is-disabled ) .menu-expand').off('hover').hover( function(e) {
			//$(this).closest('li').toggleClass('unhover');
		});
    var searchDrdn = function(curObj,event){
      var value = val;
      var proceed = true;
      var totalOpts = $(curObj).parent().children('li').length;
      var index = 0;
      var getNextOpt = function(actObj,currOpt,nextOptObj,curValue) {
        nextOpt = $(nextOptObj).next('li');
        if (nextOptObj.text().toUpperCase().indexOf(curValue) != -1 && $(nextOptObj).text().toUpperCase().indexOf(curValue) == '0') {
          $(nextOptObj).closest('.sub-ctr').find('.is-selected').removeClass('is-selected');
          nextOptObj.addClass('is-selected hilt').focus();
          nextOptObj.siblings('li').removeClass('hilt');
          $(actObj).val(nextOptObj.text());
          proceed = false;
        } else if ($(nextOptObj).next('li').length == 0 && index<totalOpts) {
          if ($(nextOptObj).next().next('li').length != 0) {
            nextOpt = $(nextOptObj).next().next('li');
          } else {
            nextOpt = $(nextOptObj).parent().children('li:first');
          }
          index = index + 1;
          getNextOpt(actObj,nextOptObj,nextOpt,curValue);
        } else {
          if (index<totalOpts) {
            index = index + 1;
            getNextOpt(actObj,nextOptObj,nextOpt,curValue);
          }
        }
      }
      if (proceed) {
        var nextOpt = $(curObj).next('li');
        if (nextOpt.length == 0) {
          if ($(curObj).next().next('li').length != 0) {
            nextOpt = $(curObj).next().next('li');
          } else {
            nextOpt = $(curObj).parent().children('li:first');
          }
        }
        getNextOpt(obj,curObj,nextOpt,value);
      }
    }
    drdnDivObj.find('li:not(.is-disabled)').off('keydown.apzevent').on( 'keydown.apzevent', function(e) {
      if (/[a-zA-Z0-9]/.test(String.fromCharCode(e.keyCode))) {
        if (!(val.length == '1' && val == String.fromCharCode(e.keyCode))) {
          val = val + String.fromCharCode(e.keyCode);
        }
        clearTimeout($.data(obj, 'timer'));
        var wait = setTimeout(function() {
          val = "";
        }, 500);
        $(obj).data('timer', wait);
        searchDrdn(this,e);
      } else {
          setDropdownVal(this,e);
      }
    });
    
	},Apz.prototype.dropdownTags = function(obj){
		$(obj).select2({
			    width: '100%'
			});
  },Apz.prototype.dropdownKeyAction = function(drdnObj,curObj,event){
    var apzObj = this;
    if(event.keyCode == 40) {
      var nextLi = $(curObj).next('li');
      var upNextLi = $(curObj).next().next('li');
      if (nextLi.length>0 || upNextLi.length>0) {
        if (nextLi.length>0) {
          nextLi.focus().addClass('hilt');
        } else if (upNextLi.length>0) {
          upNextLi.focus().addClass('hilt');
        }
        $(curObj).removeClass('hilt');
      } else {
        $(curObj).siblings('li:first').focus().addClass('hilt');
        $(curObj).removeClass('hilt');
      }
    } else if(event.keyCode == 38) {
      var prevLi = $(curObj).prev('li');
      var preprevLi = $(curObj).prev().prev('li');
      if (prevLi.length>0 || preprevLi.length>0) {
        if (prevLi.length>0) {
          prevLi.focus().addClass('hilt');
        } else if (preprevLi.length>0) {
          preprevLi.focus().addClass('hilt');
        }
        $(curObj).removeClass('hilt');
      } else {
        $(curObj).siblings('li:last').focus().addClass('hilt');
        $(curObj).removeClass('hilt');
      }
    } else if (event.keyCode == 9 || event.keyCode == 27) {
      apzObj.dropdownToggle(drdnObj);
      if ($(".drdn-extspan").length==0) {
        $(drdnObj).focus();
      }
    }
	}, Apz.prototype.dropdownToggle = function(obj) {
    var $obj = $(obj);
    var drdnDivObj = $obj.parents("#"+obj.id+"_ext:first");
    var iconObj = drdnDivObj.children('span');
		$(".drdn-open").not(drdnDivObj).each(function(){
			if($(this).hasClass('drdn-open')){
				iconObj = $(this).children('span');
				$(this).removeClass('is-open drdn-open');
		        if ($('.drdn-extspan').length>0) {
		          $("#"+$('.drdn-extspan').children().attr('original-id')+"_ext").append($('.drdn-extspan').children());
		          $('.drdn-extspan').remove();
		        }
			}
		});
		var disabled = document.getElementById(obj.id).hasAttribute("disabled");
		if(!disabled){
			drdnDivObj.toggleClass('is-open drdn-open');
			if (drdnDivObj.hasClass("drdn-open")) {
				var cloneElm = document.createElement('span');
				var cloneClass = drdnDivObj.attr('class');
				$(cloneElm).addClass(cloneClass+' drdn-extspan');
				var cloneWidth = drdnDivObj.width();
				var cloneHt = drdnDivObj.find(".sub-ctr").height();
				$(cloneElm).append(drdnDivObj.find(".sub-ctr"));
				drdnDivObj.find(".sub-ctr").remove();
				var getDropdownPos = function() {
					var objOffset = $obj.offset();
					var clonePosTop = objOffset.top;
					if ((objOffset.top + $obj.outerHeight() + cloneHt > $(window).innerHeight() + $(window).scrollTop()) && (objOffset.top > cloneHt)) {
						clonePosTop = objOffset.top - cloneHt - 10;
					} else {
						clonePosTop = objOffset.top + $obj.outerHeight() + 5;
					}
					$(cloneElm).css({
						width:cloneWidth,
						position:'absolute',
						top: clonePosTop + 'px',
						left: objOffset.left + 'px'
					});
				}
				getDropdownPos();
				$('body').append(cloneElm);
				if ($obj.scrollParent().hasClass('ftbl-bdy')) {
					$obj.scrollParent().bind('mousewheel', function() {
						if($obj.parent().hasClass("is-open")){
							return false
						} else {
							return true;
						}
					});
				}
			} else {
				drdnDivObj.append($('.drdn-extspan').children());
				$('.drdn-extspan').remove();
			}
			if ($(".drdn-extspan").length>0) {
				var extSpan = $(".drdn-extspan");
				extSpan.find('.sub-ctr li').removeClass('hilt');
				if(extSpan.find('.is-selected').length>0) {
					extSpan.find('.is-selected').focus().addClass('hilt');
				} else {
					extSpan.find('.sub-ctr li:first').focus().addClass('hilt');
				}
			}
		}
	}, Apz.prototype.dropdownApz = function(obj){
		var apzObj = this;
    	var drdnDivObj = $(obj).parents("#"+obj.id+"_ext:first");
		drdnDivObj.off('click.apzevent').on('click.apzevent', function(e) {
			if (!$(this).attr('disabled')) {
				apzObj.dropdownToggle(obj);
			}
		});
		$(obj).off('keydown.apzevent').on('keydown.apzevent', function(e) {
			if (!$(this).parents("#"+this.id+"_ext:first").attr('disabled') && (e.which=="13" || e.which == "32")) {
				if(e.keyCode == 32){
					e.preventDefault();
				}
				apzObj.dropdownToggle(obj);
			}
		});
    var val = "";
		var setDropdownVal = function(curObj,event) {
			event.preventDefault();
			if(event.keyCode == 13 || event.type == "click") {
				if (!$(curObj).hasClass("is-selected")){
					var newtext = $(curObj).text();
					$(curObj).siblings().removeClass('is-selected');
					$(curObj).addClass('is-selected');
					$(obj).val(newtext);
          apzObj.dropdownToggle(obj);
					if ($(obj).attr("onchange")) {
						$(obj).trigger("change");
					}
					event.stopPropagation();
				} else {
					apzObj.dropdownToggle(obj);
				}
				if ($(".drdn-extspan").length==0) {
					$(obj).focus();
				}
			} else {
        apzObj.dropdownKeyAction(obj,curObj,event);
      }
		}
		drdnDivObj.find('li:not(.is-disabled)').off('click.apzevent').on( 'click.apzevent', function(e) {
			setDropdownVal(this,e);
		});
		var searchDrdn = function(curObj,event){
      var value = val;
      var proceed = true;
      var totalOpts = $(curObj).parent().children('li').length;
      var index = 0;
      var getNextOpt = function(actObj,currOpt,nextOptObj,curValue) {
        nextOpt = $(nextOptObj).next('li');
        if (nextOptObj.text().toUpperCase().indexOf(curValue) != -1 && $(nextOptObj).text().toUpperCase().indexOf(curValue) == '0') {
          $(nextOptObj).parent().find('.is-selected').removeClass('is-selected');
          nextOptObj.addClass('is-selected').focus();
          $(actObj).val(nextOptObj.text());
          proceed = false;
        } else if ($(nextOptObj).next('li').length == 0 && index<totalOpts) {
          nextOpt = $(nextOptObj).parent().children('li:first');
          index = index + 1;
          getNextOpt(actObj,nextOptObj,nextOpt,curValue);
        } else {
          if (index<totalOpts) {
            index = index + 1;
            getNextOpt(actObj,nextOptObj,nextOpt,curValue);
          }
        }
      }
      if (proceed) {
        var nextOpt = $(curObj).next('li');
        if (nextOpt.length == 0) {
          nextOpt = $(curObj).parent().children('li:first');
        }
        getNextOpt(obj,curObj,nextOpt,value);
      }
    }
    drdnDivObj.find('li:not(.is-disabled)').off('keydown.apzevent').on( 'keydown.apzevent', function(e) {
      if (/[a-zA-Z0-9]/.test(String.fromCharCode(e.keyCode))) {
        if (!(val.length == '1' && val == String.fromCharCode(e.keyCode))) {
          val = val + String.fromCharCode(e.keyCode);
        }
        clearTimeout($.data(obj, 'timer'));
        var wait = setTimeout(function() {
          val = "";
        }, 500);
        $(obj).data('timer', wait);
        searchDrdn(this,e);
      } else {
        setDropdownVal(this,e);
      }
    });
	}, Apz.prototype.toggleModal = function(param) {
		 /* Params contains the below attributes
	       *** targetId,callBackObj,callBack ***
	   */
	    if (param.targetId) {
	        var $target = $("#"+param.targetId);
	        var option  = {toggle:"modal"};
	        option.target = "#"+param.targetId;
	        option.callBack = param.callBack;
	        option.callBackObj = param.callBackObj;
	        $.fn.modal.call($target, option);
	        if($target.children(".modal-dialog").length > 0){
	        	var apzObj = this;
		        $target.children(".modal-backdrop").on("click", function(){
			        apzObj.toggleModal(param);
		        });
	        }
	        if($("#"+param.targetId).hasClass('in')) {
	        	$("#"+param.targetId).attr('aria-hidden','false');
	        }
          $("#"+param.targetId+"_close").focus();
          $(".modal-backdrop").attr('tabindex','0');
	    }
	}, Apz.prototype.openTab = function(elmId) {
		this.tabAction($("#"+elmId+"_li"));
	}, Apz.prototype.openOverlay = function (obj,targetId) {
		var parentContObj = $(obj).parents('.crt-list:first');
		var parentContId = $(parentContObj).attr('id');
		var panelSecObj = $("#"+parentContId).parent();
		var panelSecId = $(panelSecObj).attr('id');
		$('#'+targetId).attr('targetoverlayid',parentContId);
		$('.height-transition').not($('height-transition-hidden')).siblings('.crt-list').find('.etb-slct').removeClass('is-open');
		$('.height-transition').not($('height-transition-hidden')).addClass('height-transition-hidden');
		if ($(obj).hasClass('etb-slct')) {
			if (!$(obj).hasClass('is-open')) {
	            var html = $("#"+targetId);
	            $(panelSecObj).append(html);
	        }
	        $(obj).toggleClass('is-open');
		} else {
			if (!$($(obj).find('.overlaytoggleicon')).hasClass('is-opened')) {
	            var html = $("#"+targetId);
	            $(panelSecObj).append(html);
	        }
		}
		if ($("#"+targetId).hasClass('height-transition-hidden')) {
	        $el = $("#"+targetId);
	        $("#"+targetId).removeClass('sno height-transition-hidden');
	        if ($el.find('.srh-inp')) {
	        	$el.find('.srh-inp').removeAttr('tabindex');
	        }
	        if (!$(obj).hasClass('etb-slct')) {
	        	$(obj).find('.overlaytoggleicon').parent().addClass('is-opened');
			}
	        var width = $($("#"+targetId).siblings('.crt-list')).width();
	        $el.css("max-height", "none");
	        $el.css("border-width", "1px");
	        $el.css("border-style", "solid");
        	$el.css('width',width);
	        // reset to 0 then animate with small delay
	        this.setTableHeight(targetId,false);
	        this.setTableHeight(targetId,true);
	        this.initDataTables($("#"+targetId).find('table[id].responsive:visible'),false);
	        var height = "";
        	if (!$el.data('overlayheight')) {
        		height = ($el.children("ul.ttl").height() + $el.find(".tabl-ctr").height()).toString() + "px";
        		$el.data('overlayheight',height);
        	} else {
        		height = $el.data('overlayheight');
        	}
	        $el.css("max-height", "0");
	        setTimeout(function() {
	            $el.css({
	                "max-height": height
	            });
	        }, 1);
	    }
	}, Apz.prototype.closeOverlay = function (obj,targetId,dataClicked) {
		var parentContObj = $(obj).parents('.crt-list:first');
		var parentContId = $(parentContObj).attr('id');
		var panelSecObj = $("#"+parentContId).parent();
		var panelSecId = $(panelSecObj).attr('id');
		$('#'+targetId).attr('targetoverlayid',parentContId);
		if ($(obj).hasClass('etb-slct')) {
	        $(obj).toggleClass('is-open');
		}
    	if (dataClicked == "Y") {
    		$('#'+parentContId).find('.etb-slct').addClass('sno');
    		$("#"+parentContId).find('.etb-slct').siblings('li').removeClass('sno');
    		$(obj).siblings('li').find('.overlaytoggleicon').parent().removeClass('is-opened');
    	}
		if (!$("#"+targetId).hasClass('height-transition-hidden')) {
	        var $el = $("#"+targetId);
	        if (!$(obj).hasClass('etb-slct')) {
	        	$(obj).find('.overlaytoggleicon').parent().removeClass('is-opened');
			}
	        $el.css("max-height", "0");
	        $el.css("border-width", "0");
	        $el.addClass("height-transition-hidden");
	        if ($el.find('.srh-inp')) {
	        	$el.find('.srh-inp').attr('tabindex',"-1");
	        }
	    }
	}, Apz.prototype.toggleOverlay = function(obj,targetId,e) {
		if (this.isNull(e)) {
			var e = {};
			e.type = "click";
		}
		if (e.type=="click" || (e.type=="keyup" && e.which=="13")) {
			if (e.type=="keyup" && e.which=="13") {
				obj = $(obj).parent()[0];
			}
			var proceed = true;
			var parentContId = $(obj).parents(".crt-list:first").attr('id');
			if(!this.isNull(parentContId)){
				if(this.isFunction(this.app["toggleOverlay_"+parentContId])){
					if (!this.isNull(parentContId) && parentContId!=targetId) {
			            proceed = this.app["toggleOverlay_"+parentContId](parentContId,targetId);
			        }
				}
			}
			if(proceed != false){
				if ($(obj).hasClass('etb-slct')) {
					if ($(obj).hasClass('is-open')){
						this.closeOverlay(obj,targetId,"");
					} else {
						this.openOverlay(obj,targetId);
					}
				} else {
					if ($(obj).find('.overlaytoggleicon').parent().hasClass('is-opened')) {
			            this.closeOverlay(obj,targetId,"");
			        } else {
			        	this.openOverlay(obj,targetId);
			        }
				}
			}
		}
	}, Apz.prototype.handleDisabled = function (event, obj) {
		if ($(obj).attr("disabled") != undefined) {
			event.preventDefault();
		}
	},Apz.prototype.slideDownTransition = function(obj){
        	$obj = $(obj);
            var parentobject = $obj.parent();
            var grparent = parentobject.parent();
            var toggleObj = parentobject.find(".tls a");
		    var $el = parentobject.children(".height-transition-hidden");
			var allAccordians = parentobject.siblings();
            if(parentobject.hasClass("acco")) {
                allAccordians.find(".collapse").removeClass("collapse").addClass("expand");
           	    allAccordians.find(".height-transition").addClass("height-transition-hidden").removeClass('htou').attr('aria-hidden','true');
                allAccordians.find(".height-transition").addClass('sno');
                allAccordians.find("ul.ttl").attr({'aria-selected':'false','aria-expanded':'false'});
            }
		    toggleObj.removeClass("expand").addClass("collapse");
            $el.removeClass("height-transition-hidden sno").addClass('htou').attr('aria-hidden','false');
            var height = $el.outerHeight();
           	var height = $el.outerHeight();
           	$el.css("max-height", "1200px");
	},Apz.prototype.slideUpTransition = function(obj) {
            var allAccordians;
            var parentobject = $(obj).parent();
            var grparent = $(parentobject).parent();
            var toggleObj = parentobject.find(".tls a"); 
		    var $el = parentobject.children(".height-transition");
		    allAccordians = parentobject.siblings();
		    if(parentobject.hasClass("acco")) {
                allAccordians.find(".collapse").removeClass("collapse").addClass("expand");
           	    allAccordians.find(".height-transition").addClass("height-transition-hidden").removeClass('htou').attr('aria-hidden','true');
                allAccordians.find(".height-transition").addClass('sno');
                allAccordians.find("ul.ttl").attr({'aria-selected':'false','aria-expanded':'false'});
            }
            $(toggleObj).removeClass("collapse").addClass("expand");
            $el.css("max-height", "0");
            $el.addClass("height-transition-hidden").removeClass("htou").attr('aria-hidden','true');
	},Apz.prototype.toggleHeader = function(){
		$("#header").toggleClass("apz-nav-open");
		this.marginSetForSideBar();
	},Apz.prototype.toggleFooter = function(){
		$("#footer").toggleClass("apz-nav-open");
		this.marginSetForSideBar();
	},Apz.prototype.keyboardAction = function(e){
		if(this.isFunction(this.app.preKeyboardAction)){
			this.app.preKeyboardAction(e);
		}
		if ($("#footer").attr('footertype')=="AUTOHIDE") {
			if (e.event == "show") {
				$("#footer").addClass("sno");
			} else if (e.event == "hide") {
				$("#footer").removeClass("sno");
			}
			this.marginSetForSideBar();
		}
		if ($("#header").attr('headertype')=="AUTOHIDE") {
			if (e.event == "show") {
				$("#header").addClass("sno");
			} else if (e.event == "hide") {
				$("#header").removeClass("sno");
			}
			this.marginSetForSideBar();
		}
		if(this.isFunction(this.app.postKeyboardAction)){
			this.app.postKeyboardAction(e);
		}
	}, Apz.prototype.setDraggable = function(params) {
		//expects id,parentsId,callBack,callBackObj
		var id = params.id;
		var callBack = params.callBack;
		var callBackObj = params.callBackObj;
		var firstObj = {};
		var calssName = "droppable_" + id;
		if (params.parentsId.length > 0) {
			for (var i = 0; i < params.parentsId.length; i++) {
				$(document.getElementById(params.parentsId[i])).addClass(calssName);
			}
		}
		var mainObj = $("."+calssName);
		$("#" + id).draggable({
			appendTo: "body",
			cursor: "move",
			helper: "clone",
			revert: "invalid",
			zIndex: 1000,
			addClasses: true
		});
		mainObj.droppable({
			greedy: true,
			addClasses: false,
			tolerance: "pointer",
			hoverClass: "drophover",
			drop: function(event, ui) {
				$(ui.draggable[0]).appendTo(this);
				callBack(ui.draggable[0], this);
				return true;
			}
		});
	}, Apz.prototype.initPaginationDropdown = function () {
		/*var apzObj = this;
		$(".pag-drdn").each(function() {
			apzObj.dropdownApz(this);
		});*/
  }, Apz.prototype.initModalEvent = function (proc) {
    $("#scr__"+proc.appId+"__"+ proc.scr + "__main [role='dialog']").off('keydown.apzevent').on('keydown.apzevent',function(e){
      var focusElms = $(this).find(':focusable');
      var prevElm = document.activeElement;
      if (e.keyCode==9 && !e.shiftKey && focusElms.index(prevElm)==focusElms.length-1) {
        $(focusElms[0]).focus();
        e.preventDefault();
      } else if (e.keyCode==9 && e.shiftKey && focusElms.index(prevElm)==0) {
        $(focusElms[focusElms.length-1]).focus();
        e.preventDefault();
      }
    });
	},
	Apz.prototype.disableControls = function(params) {
        /* Params contains the below attributes
         *** div,scr,appId ***
         */
         var apzObj = this;
        var div = '';
        if (this.isFunction(apz.app.preDisableContent)) {
            apz.app.preDisableContent();
        }
        params.appId = params.appId || this.appId;
        if (!this.isNull(params.scr)) {
            div = "scr__" + params.appId + '__' + params.scr + "__main";
        } else if (!this.isNull(params.div)) {
            div = params.div;
        }
        if (div !== '' && !this.isNull(document.getElementById(div))) {
            //Loop through the elements and disable
            var $div = $(document.getElementById(div));
            $div.find('button').each(function() {
                apzObj.disableElement(this);
            });
            $div.find('input').each(function() {
                $(this).attr("disabled","disabled");
                if($(this).attr('apztype') == "tags") {
                	apzObj.initTags(this.id);
                    $(this).parents('div.ett-tags:first').addClass('dis');
                }
            });
            $div.find('div.etb-slct').each(function() {
                $(this).addClass('select-disabled');
                $(this).attr('disabled','disabled');
            });
            $div.find('div.ett-swch').each(function() {
                $(this).addClass('disabled');
            });
            $div.find('select.etb-slcn').each(function() {
                $(this).addClass('select-disabled');
                $(this).attr('disabled','disabled');
            });
            $div.find('a.ett-hypl').each(function() {
                apzObj.disableElement(this);
            });
            $div.find('a.filebox').each(function() {
                $(this).addClass('disabled');
                $(this).siblings('a').addClass('disabled');
            });
            $div.find('textarea').each(function() {
                apzObj.disableElement(this);
            });
        }
        if (this.isFunction(apz.app.postDisableContent)) {
            apz.app.postDisableContent();
        }
    }, Apz.prototype.disableElement = function(obj) {
        /**** obj must be a DOM ******/
        if(!this.isNull(obj)) {
            var tagName = obj.tagName;
            var $obj = $(obj);
            if(tagName == "BUTTON") {
                $obj.addClass('disabled');
                $obj.attr('disabled','disabled');
            } else if(tagName == "DIV") {
                if($obj.hasClass("etb-slct")) {
                    $obj.addClass('select-disabled');
                    $obj.attr('disabled','disabled');
                } else if($obj.hasClass("ett-swch")) {
                    $obj.addClass('disabled');
                }
            } else if(tagName == "A") {
                if($obj.hasClass("ett-hypl")) {
                    $obj.attr('href','javascript:void(0)');
                    $obj.attr("disabled","disabled"); 
                    $obj.addClass('disabled'); /* colour dull, pointer events none to be added in css*/
                }
            } else if(tagName == "SELECT") {
            	if($obj.parent().hasClass('etb-slct')) {
            		$obj.parent().addClass('select-disabled');
                	$obj.parent().attr('disabled','disabled');
            	} else {
            		$obj.addClass('select-disabled');
                	$obj.attr('disabled','disabled');
            	}
            } else if(tagName == "TEXTAREA") {
                $obj.attr("disabled","disabled");
            } else if(tagName == "SPAN") {
            	if($obj.attr("apztype") == "radiogroup") {
            		$obj.find('input').attr("disabled","disabled");
            	}
            } else if(tagName == "INPUT") {
                $obj.attr("disabled","disabled");
                $obj.addClass("disabled");
                if($obj.attr('apztype') == "tags") {
                    apzObj.initTags(obj.id);
                    $obj.parents('div.ett-tags:first').addClass('dis');
                } else if($obj.attr('apztype') == "stepper") {
                	var buttons = $obj.parents('ul:first').find('button');
                	buttons.addClass('disabled');				/* for stepper*/
                	buttons.attr('disabled','disabled');
                } else if($("#"+obj.id+"_button").length > 0) {
                	$("#"+obj.id+"_button").addClass('disabled'); /* for input with button */
                	$("#"+obj.id+"_button").attr('disabled','disabled');
                } else if($obj.attr("type") == "file") {
                	$obj.parent('a').addClass('disabled');
                	$obj.parent('a').siblings('a').addClass('disabled');
                } else if($obj.hasClass("sub-elt")) {
                	$obj.parent('div').addClass('select-disabled');
                	$obj.attr('disabled','disabled');
                }
            }
        }
    }, Apz.prototype.handleBtnSpinner = function(params) {
      var proceed = true;
      var apzObj = this;
      if(apzObj.isFunction(apzObj.app.preHandleBtnSpinner)){
        proceed = apzObj.app.preHandleBtnSpinner(params);
      }
      if (proceed != false) {
        var $obj = $(params.obj);
        var objText = params.obj.innerHTML;
        $obj.text(params.text).addClass('btn-spn is-loading').attr('disabled', 'diasabled').append('<span class="loading1 loading1--sm" role="progressbar" aria-valuetext="Loading"></span>');
        setTimeout(function(){
          var stopSpinner = true;
          if(apzObj.isFunction(apzObj.app.preStopBtnSpinner)){
            stopSpinner = apzObj.app.preStopBtnSpinner(params.obj);
          }
          if (stopSpinner != false) {
            $obj.removeClass('btn-spn is-loading').removeAttr('disabled').find('.loading1').hide(1, function(){
              $(this).parent('button').text("").append(objText);
              $(this).remove();
            });
          }
        }, parseInt(params.timeout));
      }
      if(apzObj.isFunction(apzObj.app.postHandleBtnSpinner)){
        apzObj.app.postHandleBtnSpinner(params);
      }
    }, Apz.prototype.expandProgressiveDisclosure = function(obj, appendId, progDiscList) {
      var id = obj.id;
      var map = apz.scrMetaData.elmsMap;
      var appCls = "pri";
      var gparentObj = $(obj).parent().parent();
      var parObj = $(obj).parent();
      var appendObj = $("#" + appendId).removeClass('sno');
      var getAppearCls = function(appObj) {
        if ($(appObj).hasClass('sec')) {
          appCls = "sec";
        } else if ($(appObj).hasClass('ter')) {
          appCls = "ter";
        }
        return appCls;
      }
      if ($(obj).attr("type") == "CHECKBOX") {
        if ($(obj).is(':checked')) {
          if (gparentObj.siblings('.vchek-cnt').length == 0) {
            var newDiv = document.createElement('div');
            appCls = getAppearCls(gparentObj);
            newDiv.className = ('vchek-cnt ' + appCls);
            $(newDiv).attr('id', id + "_pdiv");
            gparentObj.after(newDiv);
            gparentObj.siblings('div.vchek-cnt').append(appendObj);
          } else {
            gparentObj.siblings('.vchek-cnt').show();
          }
        } else {
          gparentObj.siblings('div.vchek-cnt').hide();
        }
      } else if ($(obj).attr("type") == "radio") {
        var elmId = gparentObj.attr('id');
        var indx = gparentObj.children('span').index(parObj);
        appendId = progDiscList[indx];
        appendObj = $("#" + appendId).removeClass('sno');
        var noOfElms = gparentObj.children('span').not("span.htx").length;
        if (gparentObj.hasClass('hor')) {
          if (gparentObj.siblings('div').length != noOfElms) {
            appCls = getAppearCls(gparentObj);
            for (var a = noOfElms; a > 0; a--) {
              var newDiv = document.createElement('div');
              newDiv.className = ('rdi-cnt ' + appCls);
              $(newDiv).attr('id', elmId + "_pdiv_" + a);
              gparentObj.after(newDiv);
            }
          }
          var appendDiv = $(gparentObj).siblings('div.rdi-cnt')[indx];
          gparentObj.siblings('div').addClass('sno');
          $(appendDiv).removeClass('sno');
          $(appendDiv).append(appendObj);
        } else {
          if (parObj.siblings('div').length != noOfElms) {
            for (var a = noOfElms; a > 0; a--) {
              appCls = getAppearCls(gparentObj);
              var newDiv = document.createElement('div');
              newDiv.className = ('vrad-cnt ' + appCls);
              $(newDiv).attr('id', elmId + "_pdiv_" + a);
              $(gparentObj.children('span')[a - 1]).after(newDiv);
            }
          }
          gparentObj.children('div').addClass('sno');
          parObj.next('div.vrad-cnt').append(appendObj).removeClass('sno');
        }
      } else if (map[id].type == "DROPDOWN") {
        var noOfElms = parObj.children('span').not("span.htx").length;
        if (parObj.siblings('div').length != noOfElms) {
          for (var a = noOfElms; a > 0; a--) {
            var newDiv = document.createElement('div');
            $(newDiv).attr('id', id + "_pdiv_" + a);
            parObj.after(newDiv);
          }
        }
        var appendDiv = parObj.siblings('div');
        parObj.siblings('div').addClass('sno');
        $(appendDiv).removeClass('sno');
        $(appendDiv).append(appendObj);
      }
  }
// // UTILITY FUNCTIONS ENDS // //
