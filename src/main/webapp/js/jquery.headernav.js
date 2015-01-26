/* global $, jQuery */

(function ($) {
  'use strict';

  var DROP_CLASS = 'drop'
    , SHOW_BTN_CLASS = 'show';

  var defaults = {
        btnTitle: 'Näytä lisää',
        btnShow : '+', // TEXT/HTML
        btnHide : '-' // TEXT/HTML
      }
    , settings = {}
    , dropdownContainer
    , toggleBtn;

  var setup = function (options) {
      settings = $.extend(defaults, options);

      dropdownContainer = $('<div class="header-nav-dropdown" />');
      toggleBtn = $('<a href="#" class="toggle-dropdown" title="' + settings.btnTitle + '">' + settings.btnShow + '</a>');
    },

    handleResize = function (el) {
      var ul = el.find('ul').first()
        , li = ul.find('li')
        , pinLeft = false
        , droppedCount = 0
        , visible = false;

      var toggleMenu = function (btn, show) {
        btn.html( show ? settings.btnHide : settings.btnShow );

        if (show) {
          dropdownContainer.html(ul.clone().addClass('dropdown-menu'));
        } else {
          dropdownContainer.html('');
        }
      };

      li.each(function (index, element) {
        var el = $(element);

        if (el.position().top > 0) {
          el.addClass(DROP_CLASS);
          droppedCount++;
        } else {
          el.removeClass(DROP_CLASS);
        }
      });

      // Drop all if only one remaining
      if (li.length > 1 && $(li[1]).hasClass(DROP_CLASS)) {
        $(li[0]).addClass(DROP_CLASS);
        pinLeft = true;
      }

      el.toggleClass('pin-left', pinLeft);

      toggleMenu(toggleBtn, false);

      if (droppedCount > 0) {
        toggleBtn.addClass(SHOW_BTN_CLASS);
      } else {
        toggleBtn.removeClass(SHOW_BTN_CLASS).html(settings.btnShow);
      }

      toggleBtn.click(function (e) {
        e.preventDefault();

        var $this = $(this);

        visible = !visible;
        toggleMenu($this, visible);
      });
    };

  // Public methods
  var methods = {
    init : function (options) {
      return this.each(function (index, element) {
        var $this = $(element);

        setup(options);

        $this.append(toggleBtn);
        $this.append(dropdownContainer);
      });
    },

    resize : function () {
      return this.each(function (index, element) {
        var $this = $(element);

        setTimeout(function () {
          handleResize($this);
        }, 300);
      });
    }
  };

  $.fn.headerNav = function (methodOrOptions) {
    if ( methods[methodOrOptions] ) {
      return methods[ methodOrOptions ].apply( this, Array.prototype.slice.call( arguments, 1 ));
    } else if ( typeof methodOrOptions === 'object' || ! methodOrOptions ) {
      // Default to "init"
      return methods.init.apply( this, arguments );
    } else {
      $.error( 'Method ' +  methodOrOptions + ' does not exist on jQuery.headerNav' );
    }
  };

}(jQuery));