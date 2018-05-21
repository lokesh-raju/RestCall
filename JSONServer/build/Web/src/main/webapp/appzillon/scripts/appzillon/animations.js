Apz.Anim = function(apz) {
   ////Core Instance
   this.apz = apz;
   this.endEvents = "webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend";
};
Apz.Anim.prototype = {
   animate : function(params) {
      if (params.type == "PG") {
         this.animatePages(params);
      } else {
         this.animateDivs(params);
      }
   }, fixFixedPortions : function() {
      $(".header").addClass("headerfix");
      $(".pagebody").addClass("bodyfix");
      //RADS32ANIM
      ///Fix Sidebars
   }, freeFixedPortions : function() {
      $(".header").removeClass("headerfix");
      $(".pagebody").removeClass("bodyfix");
      //RADS32ANIM
      //Free Sidebars
   },
   ///////////////////Page Animations///////////////////////
   setPagesForAnimation : function(params) {
      window.scrollTo(0, 0);
      this.fixFixedPortions();
      ////Backup Classes and Styles
      params.origClasses = [];
      params.origStyles = [];
      ////Classes
      params.origClasses[params.oldDiv] = $("#" + params.oldDiv).attr('class');
      params.origClasses[params.newDiv] = $("#" + params.newDiv).attr('class');
       params.origClasses[params.body] = "";
    if($("body").hasClass('apz-nav-stay-left')){
        $("body").removeClass('apz-nav-stay-left')
      params.origClasses[params.body] = "apz-nav-stay-left";
    }
       if($("body").hasClass('apz-nav-stay-right')){
        $("body").removeClass('apz-nav-stay-right')
      params.origClasses[params.body] = params.origClasses[params.body] + " apz-nav-stay-right ";
    }
       //$("body").attr("class","");
      ///Styles
      params.origStyles[params.oldDiv] = $("#" + params.oldDiv).attr('style');
      params.origStyles[params.newDiv] = $("#" + params.newDiv).attr('style');
      $("#pt-main").addClass('pt-perspective');
      ////Add Required Classes
      $("#" + params.newDiv).addClass('pt-page');
      $("#" + params.oldDiv).addClass('pt-page pt-page-current');
      params.overflowx = document.getElementsByTagName("body")[0].style["overflow-x"];
      params.overflowy = document.getElementsByTagName("body")[0].style["overflow-y"];
      if (this.apz.isNull(params.overflowx)) {
         params.overflowx.overflowx = "auto";
      }
      if (this.apz.isNull(params.overflowy)) {
         params.overflowy.overflowy = "auto";
      }
      document.getElementsByTagName("body")[0].style["overflow"] = "hidden";
      ////Hide Sidebar
      if (params.hideNewSideBar) {
         $("div#" + pdiv + " .rolepage").each(function(Pelm, val) {
            if ($(this).hasClass("open-sidebar")) {
               $(this).removeClass("open-sidebar");
               params.newSideBarHidden = true;
            }
         });
      }
   }, animatePages : function(params) {
      ///Prepare Divs
      this.setPagesForAnimation(params);
      this.fixFixedPortions();
      var myObj = this;
      var lclasses = this.getAnimationClasses(params.animation);
      var loutclass = lclasses[0];
      var linclass = lclasses[1];
      ////Attach Additional Classses Just Before Animation..
      $("#" + params.newDiv).addClass('pt-page-current');
      $("#" + params.newDiv).removeClass('ssp sno');
      //RADS32ANIM
      this.apz.readjustHeight();
      $("#" + params.oldDiv).addClass(loutclass).on(this.endEvents, function() {
         $("#" + params.oldDiv).off(myObj.endEvents);
         params.oldEnded = true;
         myObj.endOfPageAnimation(params);
      });
      $("#" + params.newDiv).addClass(linclass).on(this.endEvents, function() {
         $("#" + params.newDiv).off(myObj.endEvents);
         params.newEnded = true;
         myObj.endOfPageAnimation(params);
      });
   }, endOfPageAnimation : function(params) {
      if ((params.oldEnded) && (params.newEnded)) {
         this.resetPages(params);
         ////Call apz's Show Screen
         this.apz.scrShowScreen(params);
      }
   }, resetPages : function(params) {
      $("#" + params.oldDiv).html("");
      $("#" + params.oldDiv).removeClass("pt-page pt-page-current");
      $("#" + params.newDiv).removeClass("pt-page pt-page-current");
      ////Restore Original  Classes and Styles
      $("#" + params.oldDiv).removeAttr('class');
      $("#" + params.newDiv).removeAttr('class');
      $("#" + params.oldDiv).addClass(params.origClasses[params.oldDiv]);
      $("#" + params.newDiv).addClass(params.origClasses[params.newDiv]);
      ////Additioanl Classes..
      $("#pt-main").removeAttr('class');
      ////Show Sidebar if Required,..
      if (params.newSideBarHidden) {
         $("div#" + params.newDiv + " .rolepage").each(function(elm, val) {
            $(this).addClass("open-sidebar");
         });
      }
      this.freeFixedPortions();
      document.getElementsByTagName("body")[0].style["overflow-x"] = params.overflowx;
      document.getElementsByTagName("body")[0].style["overflow-y"] = params.overflowy;
   },
   ///////////////////Div Animations////////////////////////
   fixDiv : function(div) {
      try {
         var posStyle = $("#" + div).css("position");
         var pos = {};
         pos.left = $("#" + div).position().left;
         pos.top = $("#" + div).position().top;
         pos.width = $("#" + div).width();
         pos.height = $("#" + div).height();
         if (pos.height == 0) {
            pos.height = 1;
         }
         pos.marginleft = $("#" + div).css("margin-left");
         pos.marginright = $("#" + div).css("margin-right");
         console.log("Pos Type :" + posStyle);
         
         if (posStyle != "absolute") {
            $("#" + div).addClass("apzposition");
            $("#" + div).css("width", "100%");
            //RADS32ANIM
            $("#" + div).css("left", pos.left + "px");
            $("#" + div).css("top", pos.top + "px");
            $("#" + div).css("height", "3000px");
         }
         
      } catch(e) {
         console.log("Error :" + e.message);
      }
      return pos;
   }, animateDivs : function(params) {
      ///Prepare Divs
      this.setDivsForAnimation(params);
      var myObj = this;
      ////make them Visible
      $("#" + params.newDiv).removeClass("sno ssp");
      $("#" + params.oldDiv).removeClass("sno ssp");
      var lclasses = this.getAnimationClasses(params.animation);
      var loutclass = lclasses[0];
      var linclass = lclasses[1];
      $("#" + params.oldDiv).addClass(loutclass).on(this.endEvents, function() {
         $("#" + params.oldDiv).off(myObj.endEvents);
         params.oldEnded = true;
         if (params.newEnded) {
            myObj.endOfDivAnimation(params);
         }
      });
      $("#" + params.newDiv).addClass(linclass).on(this.endEvents, function() {
         $("#" + params.newDiv).off(myObj.endEvents);
         params.newEnded = true;
         if (params.oldEnded) {
            myObj.endOfDivAnimation(params);
         }
      });
   }, setDivsForAnimation : function(params) {
      ////Get Parent
      params.parentDiv = document.getElementById(params.oldDiv).parentNode.id;
      ////Backup Classes and Styles
      params.origClasses = [];
      params.origStyles = [];
      ////Classes
      params.origClasses[params.parentDiv] = $("#" + params.parentDiv).attr('class');
      params.origClasses[params.oldDiv] = $("#" + params.oldDiv).attr('class');
      params.origClasses[params.newDiv] = $("#" + params.newDiv).attr('class');
      ///Styles
      params.origStyles[params.parentDiv] = $("#" + params.parentDiv).attr('style');
      params.origStyles[params.oldDiv] = $("#" + params.oldDiv).attr('style');
      params.origStyles[params.newDiv] = $("#" + params.newDiv).attr('style');
      
      // Position absolute shouldn't be set for Parent div
      //this.fixDiv(params.parentDiv);
      
      ///Fix Them
      this.fixDiv(params.oldDiv);
      this.fixDiv(params.newDiv);
      //Hiding the scroll bar will bring out a jerk
      //$("#" + params.parentDiv).css("overflow", "hidden");
      //RADS32ANIM
      ///Backup Overflow of Body as well.
      params.overflow = document.getElementsByTagName("body")[0].style["overflow"];
      params.overflowx = document.getElementsByTagName("body")[0].style["overflow-x"];
      params.overflowy = document.getElementsByTagName("body")[0].style["overflow-y"];
      if (this.apz.isNull(params.overflow)) {
         params.overflowx.overflow = "auto";
      }
      if (this.apz.isNull(params.overflowx)) {
         params.overflowx.overflowx = "auto";
      }
      if (this.apz.isNull(params.overflowy)) {
         params.overflowy.overflowy = "auto";
      }
      //Hiding the scroll bar will bring out a jerk
      //document.getElementsByTagName("body")[0].style["overflow"] = "hidden";
      $("#" + params.parentDiv).addClass("pt-sec-perspective");
      $("#" + params.newDiv).addClass("pt-sec pt-sec-current");
      $("#" + params.oldDiv).addClass("pt-sec pt-sec-current");
   }, endOfDivAnimation : function(params) {
      if ((params.oldEnded) && (params.newEnded)) {
         this.resetDivs(params);
         ////Call apz's Show Screen
         if (params.type != "DIV") {
            this.apz.scrShowScreen(params);
         }
      }
   }, resetDivs : function(params) {
      for (var div in params.origClasses) {
        
         if (!this.apz.isNull(div)) {
            $("#" + div).removeAttr('class');
            $("#" + div).addClass(params.origClasses[div]);
            ////Styles
            $("#" + div).attr("style", "");
            $("#" + div).removeAttr("style")
            $("#" + div).attr("style", params.origStyles[div]);
         }
         
      }
      //RADS32ANIM
      document.getElementsByTagName("body")[0].style["overflow"] = params.overflow;
      document.getElementsByTagName("body")[0].style["overflow-x"] = params.overflowx;
      document.getElementsByTagName("body")[0].style["overflow-y"] = params.overflowy;
      $("#" + params.oldDiv).addClass("sno");
      $("#" + params.newDiv).removeClass("sno ssp");
   },
   ///////////////////Animations Classes////////////////////
   getAnimationClasses : function(animation) {
      var loutclass = '';
      var linclass = '';
      var lclasses = [];
      switch( animation ) {
         case 1:
            loutclass = 'pt-page-moveToLeft';
            linclass = 'pt-page-moveFromRight';
            break;
         case 2:
            loutclass = 'pt-page-moveToRight';
            linclass = 'pt-page-moveFromLeft';
            break;
         case 3:
            loutclass = 'pt-page-moveToTop';
            linclass = 'pt-page-moveFromBottom';
            break;
         case 4:
            loutclass = 'pt-page-moveToBottom';
            linclass = 'pt-page-moveFromTop';
            break;
         case 5:
            loutclass = 'pt-page-fade';
            linclass = 'pt-page-moveFromRight pt-page-ontop';
            break;
         case 6:
            loutclass = 'pt-page-fade';
            linclass = 'pt-page-moveFromLeft pt-page-ontop';
            break;
         case 7:
            loutclass = 'pt-page-fade';
            linclass = 'pt-page-moveFromBottom pt-page-ontop';
            break;
         case 8:
            loutclass = 'pt-page-fade';
            linclass = 'pt-page-moveFromTop pt-page-ontop';
            break;
         case 9:
            loutclass = 'pt-page-moveToLeftFade';
            linclass = 'pt-page-moveFromRightFade';
            break;
         case 10:
            loutclass = 'pt-page-moveToRightFade';
            linclass = 'pt-page-moveFromLeftFade';
            break;
         case 11:
            loutclass = 'pt-page-moveToTopFade';
            linclass = 'pt-page-moveFromBottomFade';
            break;
         case 12:
            loutclass = 'pt-page-moveToBottomFade';
            linclass = 'pt-page-moveFromTopFade';
            break;
         case 13:
            loutclass = 'pt-page-moveToLeftEasing pt-page-ontop';
            linclass = 'pt-page-moveFromRight';
            break;
         case 14:
            loutclass = 'pt-page-moveToRightEasing pt-page-ontop';
            linclass = 'pt-page-moveFromLeft';
            break;
         case 15:
            loutclass = 'pt-page-moveToTopEasing pt-page-ontop';
            linclass = 'pt-page-moveFromBottom';
            break;
         case 16:
            loutclass = 'pt-page-moveToBottomEasing pt-page-ontop';
            linclass = 'pt-page-moveFromTop';
            break;
         case 17:
            loutclass = 'pt-page-scaleDown';
            linclass = 'pt-page-moveFromRight pt-page-ontop';
            break;
         case 18:
            loutclass = 'pt-page-scaleDown';
            linclass = 'pt-page-moveFromLeft pt-page-ontop';
            break;
         case 19:
            loutclass = 'pt-page-scaleDown';
            linclass = 'pt-page-moveFromBottom pt-page-ontop';
            break;
         case 20:
            loutclass = 'pt-page-scaleDown';
            linclass = 'pt-page-moveFromTop pt-page-ontop';
            break;
         case 21:
            loutclass = 'pt-page-scaleDown';
            linclass = 'pt-page-scaleUpDown pt-page-delay300';
            break;
         case 22:
            loutclass = 'pt-page-scaleDownUp';
            linclass = 'pt-page-scaleUp pt-page-delay300';
            break;
         case 23:
            loutclass = 'pt-page-moveToLeft pt-page-ontop';
            linclass = 'pt-page-scaleUp';
            break;
         case 24:
            loutclass = 'pt-page-moveToRight pt-page-ontop';
            linclass = 'pt-page-scaleUp';
            break;
         case 25:
            loutclass = 'pt-page-moveToTop pt-page-ontop';
            linclass = 'pt-page-scaleUp';
            break;
         case 26:
            loutclass = 'pt-page-moveToBottom pt-page-ontop';
            linclass = 'pt-page-scaleUp';
            break;
         case 27:
            loutclass = 'pt-page-rotateRightSideFirst';
            linclass = 'pt-page-moveFromRight pt-page-delay200 pt-page-ontop';
            break;
         case 28:
            loutclass = 'pt-page-rotateLeftSideFirst';
            linclass = 'pt-page-moveFromLeft pt-page-delay200 pt-page-ontop';
            break;
         case 29:
            loutclass = 'pt-page-rotateTopSideFirst';
            linclass = 'pt-page-moveFromTop pt-page-delay200 pt-page-ontop';
            break;
         case 30:
            loutclass = 'pt-page-rotateBottomSideFirst';
            linclass = 'pt-page-moveFromBottom pt-page-delay200 pt-page-ontop';
            break;
         case 31:
            loutclass = 'pt-page-flipOutRight';
            linclass = 'pt-page-flipInLeft pt-page-delay500';
            break;
         case 32:
            loutclass = 'pt-page-flipOutLeft';
            linclass = 'pt-page-flipInRight pt-page-delay500';
            break;
         case 33:
            loutclass = 'pt-page-flipOutTop';
            linclass = 'pt-page-flipInBottom pt-page-delay500';
            break;
         case 34:
            loutclass = 'pt-page-flipOutBottom';
            linclass = 'pt-page-flipInTop pt-page-delay500';
            break;
         case 35:
            loutclass = 'pt-page-rotateFall pt-page-ontop';
            linclass = 'pt-page-scaleUp';
            break;
         case 36:
            loutclass = 'pt-page-rotateOutNewspaper';
            linclass = 'pt-page-rotateInNewspaper pt-page-delay500';
            break;
         case 37:
            loutclass = 'pt-page-rotatePushLeft';
            linclass = 'pt-page-moveFromRight';
            break;
         case 38:
            loutclass = 'pt-page-rotatePushRight';
            linclass = 'pt-page-moveFromLeft';
            break;
         case 39:
            loutclass = 'pt-page-rotatePushTop';
            linclass = 'pt-page-moveFromBottom';
            break;
         case 40:
            loutclass = 'pt-page-rotatePushBottom';
            linclass = 'pt-page-moveFromTop';
            break;
         case 41:
            loutclass = 'pt-page-rotatePushLeft';
            linclass = 'pt-page-rotatePullRight pt-page-delay180';
            break;
         case 42:
            loutclass = 'pt-page-rotatePushRight';
            linclass = 'pt-page-rotatePullLeft pt-page-delay180';
            break;
         case 43:
            loutclass = 'pt-page-rotatePushTop';
            linclass = 'pt-page-rotatePullBottom pt-page-delay180';
            break;
         case 44:
            loutclass = 'pt-page-rotatePushBottom';
            linclass = 'pt-page-rotatePullTop pt-page-delay180';
            break;
         case 45:
            loutclass = 'pt-page-rotateFoldLeft';
            linclass = 'pt-page-moveFromRightFade';
            break;
         case 46:
            loutclass = 'pt-page-rotateFoldRight';
            linclass = 'pt-page-moveFromLeftFade';
            break;
         case 47:
            loutclass = 'pt-page-rotateFoldTop';
            linclass = 'pt-page-moveFromBottomFade';
            break;
         case 48:
            loutclass = 'pt-page-rotateFoldBottom';
            linclass = 'pt-page-moveFromTopFade';
            break;
         case 49:
            loutclass = 'pt-page-moveToRightFade';
            linclass = 'pt-page-rotateUnfoldLeft';
            break;
         case 50:
            loutclass = 'pt-page-moveToLeftFade';
            linclass = 'pt-page-rotateUnfoldRight';
            break;
         case 51:
            loutclass = 'pt-page-moveToBottomFade';
            linclass = 'pt-page-rotateUnfoldTop';
            break;
         case 52:
            loutclass = 'pt-page-moveToTopFade';
            linclass = 'pt-page-rotateUnfoldBottom';
            break;
         case 53:
            loutclass = 'pt-page-rotateRoomLeftOut pt-page-ontop';
            linclass = 'pt-page-rotateRoomLeftIn';
            break;
         case 54:
            loutclass = 'pt-page-rotateRoomRightOut pt-page-ontop';
            linclass = 'pt-page-rotateRoomRightIn';
            break;
         case 55:
            loutclass = 'pt-page-rotateRoomTopOut pt-page-ontop';
            linclass = 'pt-page-rotateRoomTopIn';
            break;
         case 56:
            loutclass = 'pt-page-rotateRoomBottomOut pt-page-ontop';
            linclass = 'pt-page-rotateRoomBottomIn';
            break;
         case 57:
            loutclass = 'pt-page-rotateCubeLeftOut pt-page-ontop';
            linclass = 'pt-page-rotateCubeLeftIn';
            break;
         case 58:
            loutclass = 'pt-page-rotateCubeRightOut pt-page-ontop';
            linclass = 'pt-page-rotateCubeRightIn';
            break;
         case 59:
            loutclass = 'pt-page-rotateCubeTopOut pt-page-ontop';
            linclass = 'pt-page-rotateCubeTopIn';
            break;
         case 60:
            loutclass = 'pt-page-rotateCubeBottomOut pt-page-ontop';
            linclass = 'pt-page-rotateCubeBottomIn';
            break;
         case 61:
            loutclass = 'pt-page-rotateCarouselLeftOut pt-page-ontop';
            linclass = 'pt-page-rotateCarouselLeftIn';
            break;
         case 62:
            loutclass = 'pt-page-rotateCarouselRightOut pt-page-ontop';
            linclass = 'pt-page-rotateCarouselRightIn';
            break;
         case 63:
            loutclass = 'pt-page-rotateCarouselTopOut pt-page-ontop';
            linclass = 'pt-page-rotateCarouselTopIn';
            break;
         case 64:
            loutclass = 'pt-page-rotateCarouselBottomOut pt-page-ontop';
            linclass = 'pt-page-rotateCarouselBottomIn';
            break;
         case 65:
            loutclass = 'pt-page-rotateSidesOut';
            linclass = 'pt-page-rotateSidesIn pt-page-delay200';
            break;
         case 66:
            loutclass = 'pt-page-rotateSlideOut';
            linclass = 'pt-page-rotateSlideIn';
            break;
         case 67:
            loutclass = 'pt-page-scaleDownCenter';
            linclass = 'pt-page-scaleUpCenter pt-page-delay400';
            break;
      }
      lclasses[0] = loutclass;
      lclasses[1] = linclass;
      return lclasses;
   }
};