/*global $, jQuery, Init, modalData */

(function () {
  'use strict';

  var generateModal, localization, validateForm, modalContent, modalType, delay;

  /**
   *
   * Generate modal
   * ==============
   * - Gets modalContent containing all HTML
   * - Uses jsRender to render template into the modal container
   * - Shows modal template with the defined content
   *
   * */
  generateModal = function (modalContent, modalType) {
    $("#modal-container").html($("#modal-template").render(modalContent));
    $(".initiative-modal").loadModal(modalType);
    return false;
  };

  localization = {
    getSubmitInfo: function (locale) {
      if (locale === 'sv') {
        return "Det verkar räcka längre än väntat att utföra funktionen. Vänligen vänta en stund.";
      } else {
        return "Toiminnon suorittaminen näyttäisi kestävän odotettua kauemmin. Ole hyvä ja odota hetki.";
      }
    },
    getSubmitWarning: function (locale) {
      if (locale === 'sv') {
        return "Det gick inte att utföra funktionen. Vänligen försök på nytt om några minuter.";
      } else {
        return "Toimintoa ei voitu suorittaa. Ole hyvä ja yritä uudelleen muutaman minuutin kuluttua.";
      }
    }
  };

  /**
   *
   * Validate organizer block
   * ========================
   * - Generates validation error-message, if user has tried to submit the form dispite the errors showing in email-addresses.
   *
   * */
  validateForm = function (formBlock) {
    if (formBlock === "organizers") {
      if ($('.email-input').find('.invalid').length > 0) {
        var authorArea = $('.initiative-authors-area');
        if (authorArea.find('.msg-error').length === 0) {
          authorArea.prepend('<div class="system-msg msg-error">Sähköpostiosoitteissa on virheitä.</div>');
        }
      } else {
        $('form#form-initiative').submit();
      }
    }
  };

  /**
   * Common delay
   * ============
   *
   * */
  delay = (function () {
    var timer = 0;
    return function (callback, ms) {
      clearTimeout(timer);
      timer = setTimeout(callback, ms);
    };
  }());

  $(document).ready(function () {
    // Define general variables
    var $body, speedFast, speedSlow, speedVeryFast, speedAutoHide, vpHeight, vpWidth, validateEmail, locale, isIE7, $topRibbon, myConsole;

    $body = $('body');
    speedFast = '200'; // General speeds for animations
    speedVeryFast = '10';
    speedSlow = 'slow';
    speedAutoHide = '2000'; // Delay for hiding success-messages (if enabled)
    vpHeight = $(window).height(); // Viewport height
    vpWidth = $(window).width(); // Viewport width
    locale = Init.getLocale();     // Current locale: fi, sv
    isIE7 = $('html').hasClass('ie7'); // Boolean for IE7. Used browser detection instead of jQuery.support().

    // Console fix for IE
    if (typeof console === "undefined") {
      myConsole = {
        log: function () {}
      };
    } else {
      myConsole = console;
    }

    /**
     * Common helpers
     * ==============
     */
    // Wait a while and hide removable message
    if ($('.auto-hide').length > 0) {
      setTimeout(function () {
        $('.auto-hide').fadeOut(speedSlow);
      }, speedAutoHide);
    }

    // Validate emails
    validateEmail = function (email) {
      var re;
      re = /^(([^<>()\[\]\\.,;:\s@\"]+(\.[^<>()\[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
      return re.test(email);
    };

    // Switch content between element's HTML and data-alttext -attribute
    // When elem-argument is used, function switches content inside elem.
    jQuery.fn.switchContent = function (elem) {
      var switcher, temp;

      if (!elem) {
        elem = false;
      }

      switcher = $(this);
      temp = switcher.data('alttext');

      if (elem) {
        switcher.data('alttext', elem.html());
        elem.html(temp);
      } else {
        switcher.data('alttext', switcher.html());
        switcher.html(temp);
      }
    };

    // Fire supportVoteGraph
    if (window.supportVoteGraph && window.supportVoteGraph.votes.length > 0) {
      $(window).on('resize', function () {
        $('#supportVotesGraph').html('').supportVoteGraph({
          data: window.supportVoteGraph,
          color: '#087480',
          colorHl: '#bc448e',
          width : $('#supportVotesGraph').parent().width() || 960,
          height : 250,
          leftgutter : 50,
          rightgutter : 30,
          bottomgutter : 20,
          topgutter : 20,
          cumulative : true,
          max : 50000
        });
      }).trigger('resize');
    }

    /**
     *  Prevent double clicks
     */
    $("button").live('click', function () {
      var btnClicked, firstBtnInForm, $loader, $submitInfo, $submitWarning, siblingButtons;
      btnClicked = $(this);

      // Disable in some cases
      if (!btnClicked.hasClass('disable-dbl-click-check')) {
        // We may have more than one submit button. For example in create.
        firstBtnInForm = btnClicked.parents('form').find('button:first');
        siblingButtons = btnClicked.siblings('.small-button, .large-button');
        $loader = $('<span class="loader" />');
        $submitInfo = $('<div class="system-msg msg-info">' + localization.getSubmitInfo(Init.getLocale()) + '</div>');
        $submitWarning = $('<div id="submit-warning" class="system-msg msg-warning">' + localization.getSubmitWarning(Init.getLocale()) + '</div>');

        $("#submit-warning").remove(); // Clear warning

        btnClicked.css('position', 'relative'); // Set button relative for loader animation position

        // Separate classes for styling and function, so that would not mess up classes and functions.
        if (!btnClicked.hasClass("clicked")) {
          btnClicked.addClass("disabled clicked");
          siblingButtons.addClass("disabled clicked");
          siblingButtons.click(function () {
            return false;
          });
          btnClicked.append($loader);
          setTimeout(function () {
            firstBtnInForm.before($submitInfo);
          }, 5000);
          setTimeout(function () {
            btnClicked.removeClass("disabled clicked");
            siblingButtons.removeClass("disabled clicked");
            $loader.remove();
            $submitInfo.remove();
            firstBtnInForm.before($submitWarning);
          }, 30000);
        } else {
          return false;
        }
      }
    });

    // Remove system message
    $('.close-msg').click(function () {
      var parent = $(this).closest('div').fadeOut('speedSlow', function () {
        parent.remove();
      });
    });

    // Action for external links
    $('a.external').click(function () {
      window.open($(this).attr('href'));
      return false;
    });

    // CSS3 animation for progress bars
    // Remove timeout in production. Only needed when using LESS-files
    setTimeout(function () {
      $('.bar').removeClass('js-animate');
    }, 50);


    // TOP ribbon
    $topRibbon = $('.debug-ribbon.top.fixed');
    $topRibbon.after('<div style="height:' + $topRibbon.outerHeight() + 'px" />').css('position', 'fixed');


    /**
     * Toggle form help texts
     * ======================
     *  - Help text fires up when user clicks help-icon or focuses to a text-input or a textarea
     *  - Both inputs and helps needs to be descendants of the same parent element ".input-block-content"
     *  - Adjust container's height if adjustment is needed
     *  Known issues:
     *  - In some case when removing link-rows help text overflows
     */
    (function () {
      var $allHelps, toggleHelpTexts;

      $allHelps = $('.input-block-extra');

      toggleHelpTexts = function (elem, close) {

        var initPadding, initMargin, thisElem, elemParent, elemHeight, hd, parentBlock, elemParentTopPos;

        initPadding = 25; // hardcoded value from CSS: .input-block:            padding:25px
        initMargin = 35; // hardcoded value from CSS: .input-block-content: margin-top:2em -> 35 (px)

        thisElem = elem.children('.input-block-extra-content');
        elemParent = elem.closest(".input-block-content");
        parentBlock = elem.closest(".input-block");
        elemHeight = thisElem.outerHeight();
        hd = elemParent.height() - elemHeight;

        if (elem.is(':visible')) {
          if (close) {
            elem.fadeOut(speedFast, function () {
              // Reset the height if needed
              if (hd < 0) {
                parentBlock.stop(false, true).animate({
                  paddingBottom: initPadding.toString() + 'px'
                }, speedFast, function () {
                  parentBlock.removeAttr('style'); // clear styles after animation
                });
              }
            });
          }
        } else {
          $allHelps.fadeOut(speedFast);
          elem.fadeIn(speedFast);

          elemParentTopPos = 0;
          if (elemParent.position() !== null) {
            elemParentTopPos = elemParent.position().top;
          }

          elemHeight = elem.children('.input-block-extra-content').outerHeight();
          hd = (elemHeight + elemParentTopPos + initMargin) - parentBlock.height();

          // Adjust the height if needed
          if (hd > initPadding) {
            parentBlock.stop(false, true).animate({
              paddingBottom: hd.toString() + "px"
            }, speedFast, function () {
              parentBlock.css('min-height', parentBlock.height() + 'px'); // create min-height so that when opening another smaller help container would not collapse
            });
          }
        }
      };

      // Matches with data-name of the help-icon and the class-name of the help-text container
      $('.help').click(function () {
        var help, $thisHelp;
        help = $(this).data('name');
        $thisHelp = $('.input-block-extra.' + help);

        toggleHelpTexts($thisHelp, true);
      });

      // Matches class-name "input-block-extra" within the same block
      $('input[type=text],textarea').live('focus', function () {
        var $thisHelp = $(this).parents('.input-block-content:first').find('.input-block-extra:first');
        toggleHelpTexts($thisHelp, false);
      });
    }());

    /**
     *  Toggle dropdown menus
     *  =====================
     * */
    (function () {
      var clearMenus, $dToggle, $dMenu, clickedVisible;
      $dToggle = $('.dropdown-toggle');
      $dMenu = $('.dropdown-menu');

      clearMenus = function () {
        $dMenu.hide();
        $dToggle.removeClass('active');
      };

      $dToggle.click(function (e) {
        if (!$(this).next($dMenu).is(':visible')) {
          clickedVisible = true;
        } else {
          clickedVisible = false;
        }

        clearMenus();

        if (clickedVisible) {
          $(this)
            .addClass('active')
            .next($dMenu).show();
        }
        return false;
      });
      // Off clicking closes menus
      $('body').on('click', function (e) {
        if ($dMenu.is(':visible')) {
          clearMenus();
        }
      });

    }());

    /**
     *
     * Change the font-size
     * ====================
     * - Uses jQuery Cookie: https://github.com/carhartl/jquery-cookie
     * - Toggles a class for body-element (font-size-small, font-size-medium, font-size-large)
     * - Sets and gets fontSizeClass-cookie
     *
     * */
    (function () {
      var $fontSizeToggler, fontSizeClass;

      $fontSizeToggler = $('.font-size-toggle a');
      fontSizeClass = "font-size-medium";

      // Get cookie value for the fontSize
      if ($.cookie("fontSizeClass") !== null) {
        fontSizeClass = $.cookie("fontSizeClass");
      }

      $('.font-size-toggle .' + fontSizeClass).addClass('active');
      $body.addClass(fontSizeClass);

      $fontSizeToggler.click(function () {
        var thisItem = $(this);

        $fontSizeToggler.find('span').removeClass('active');
        thisItem.find('span').addClass('active');

        if (thisItem.hasClass('font-size-small-link')) {
          fontSizeClass = "font-size-small";
        } else if (thisItem.hasClass('font-size-large-link')) {
          fontSizeClass = "font-size-large";
        } else {
          fontSizeClass = "font-size-medium";
        }

        $body.removeClass("font-size-small font-size-medium font-size-large").addClass(fontSizeClass);

        // Set current fontSize as a cookie value.
        $.cookie("fontSizeClass", fontSizeClass, {
          expires: 30,
          path: '/'
        });

        return false;
      });
    }());



    /**
     *
     * Toggle alternative language form fields
     * =======================================
     *
     * */
    (function () {
      var $altLangLink = $('#show-alternative-lang');

      $altLangLink.click(function () {
        var thisLink = $(this);
        thisLink.switchContent();

        $('.alt-lang').stop(false, true).slideToggle({
          duration: speedFast,
          easing: 'easeOutExpo'
        });

        if (thisLink.data('translation')) {
          thisLink.attr('data-translation', 'false');
        } else {
          thisLink.attr('data-translation', 'true');
        }

        return false;
      });

      // Use hide-button if initiative has translation
      if ($altLangLink.data('translation')) {
        $altLangLink.switchContent();
        $('.alt-lang').show();
      }
    }());


    /**
     *
     * Add and remove link
     * ===================
     * - Uses jsRender to render link-row template
     * - Handles adding and removing a link-row
     *
     * */
    (function () {
      var $altLangLink, $linkContainer, index, createRemoveLink, createLinkRow;

      $altLangLink = $('#show-alternative-lang');
      $linkContainer = $('#link-container');
      index = $linkContainer.data("index");

      createRemoveLink = function (elem) {
        var linkHeight, $removeLink;

        linkHeight = elem.innerHeight();
        $removeLink = $('<a class="remove-link ignoredirty" style="height:' + linkHeight + 'px">x</a>');
        elem.wrap('<div style="position:relative;">');
        elem.after($removeLink);
      };

      // Use jsRender to render a new link row
      createLinkRow = function () {
        var links = {
          linkIndex: index.toString()
        };
        $linkContainer.append($("#linkTemplate").render(links));

        if ($altLangLink.data('translation')) {
          $('.alt-lang').show();
        }

        index += 1;
      };

      // Create one as default and other ones with a click.
      if ($linkContainer.length > 0) {
        createLinkRow();
      }

      $('#add-new-link').live('click', function () {
        createLinkRow();

        return false;
      });

      // Remove link (clear values and hide fields)
      $('.remove-link').live('click', function () {
        var removedLink = $(this).closest('.add-link');

        removedLink.slideUp(speedFast, function () {
          // Animation complete.
        }).find("input").attr("value", "");

        return false;
      });
    }());



    /**
     *
     * Modal: jQuery tools - Overlay
     * =============================
     * - Load modal with defined configurations
     * - Aligns modal to the middle of the viewport
     * - Adds scrollbars to modal content if it doesn't fit in the viewport
     * - Types
     *  - minimal: for simple confirms
     *  - full: for accept invitation
     *
     * FIXME:
     * - Form buttons are pushed to the bottom when scrollbars are visible. Add some space after the form.
     *
     * */
    jQuery.fn.loadModal = function (modalType) {
      var modal, topPos, modalFixed, adjustModal, maxTopPos, modalHeight, $modalContent;
      modal = $(this);
      $modalContent = modal.children('.modal-content');

      modalHeight = function () {
        return modal.height();
      };
      topPos = function () {
        if ((modalType === "full") && (modalHeight() < vpHeight)) {
          return Math.floor((vpHeight - modalHeight()) / 2);
        } else if (modalType === "minimal") {
          // 10% of viewport's size seems to be fine
          return Math.floor(0.1 * vpHeight);
        } else {
          return 10; // 10 px
        }

      };
      modalFixed = function () {
        if (modalType === "full") {
          return false;
        } else {
          return true;
        }
      };

      // Adjust modal's position and height
      adjustModal = function (modal, $modalContent, topPos) {
        var modalPosX;
        modalPosX = (vpWidth - modal.width()) / 2;
        modal.css('left', modalPosX);

        if (modalType === "minimal") {
          if (modal.height() > vpHeight) {
            modal.css('position', 'absolute');
          } else {
            modal.css('position', 'fixed');
          }
        }
      };

      modal.overlay({
        fixed: modalFixed(), // modal position
        top: topPos(), // custom top position
        mask: { // custom mask
          color: '#000', // you might also consider a "transparent" color for the mask
          loadSpeed: speedFast, // load mask a little faster
          opacity: 0.5 // very transparent
        },
        onBeforeLoad: function () { // In some cases close-link has href for NOSCRIPT-users.
          modal.find('.close').removeAttr("href"); // Removing href to prevent any actions. Preventing default-action did not work.
          $('.binder').each(function () {
            $(this).bindCheckbox(); // Bind checkbox with submit button (used in remove support votes for example)
          });
        },
        closeOnClick: false, // disable this for modal dialog-type of overlays
        load: true // load it immediately after the construction
      }).addClass(modalType);

      // Adjust modal after load
      adjustModal(modal, $modalContent, topPos());

      // Adjust modal when user resizes viewport
      $(window).bind('resize', function () {
        vpHeight = $(this).height();
        vpWidth = $(this).width();
        modal.css('top', topPos().toString() + 'px');

        adjustModal(modal, $modalContent, topPos());
      });
    };


    /**
     *
     * Modal-loaders
     * =============
     * - Uses class-based actions
     * - Modals loaded also on page load if correct data exists
     * - Calls generateModal() with modalData variable which includes all HTML content for the modal
     *
     * */

    // Initiative saved and invitations sent successfully
    if (typeof modalData !== 'undefined' && typeof modalData.requestMessage !== 'undefined') {
      generateModal(modalData.requestMessage(), 'minimal');
    }

    // Confirm invitation decline
    $('.invitation-decline-confirm').click(function () {
      try {
        generateModal(modalData.invitationDecline(), 'minimal');
        return false;
      } catch (e) {
        myConsole.log(e);
      }
    });

    // Accept invitation
    if (typeof modalData !== 'undefined' && typeof modalData.invitationAccept !== 'undefined') {
      generateModal(modalData.invitationAccept(), 'full');
    }

    // Confirm send to OM
    $('.send-to-om-confirm').click(function () {
      try {
        generateModal(modalData.sendToOmConfirm(), 'minimal');
        return false;
      } catch (e) {
        myConsole.log(e);
      }
    });

    // OM accepts
    $('.om-accept-initiative').click(function () {
      try {
        generateModal(modalData.omAcceptInitiative(), 'full');
        return false;
      } catch (e) {
        myConsole.log(e);
      }
    });

    // OM rejects
    $('.om-reject-initiative').click(function () {
      try {
        generateModal(modalData.omRejectInitiative(), 'full');
        return false;
      } catch (e) {
        myConsole.log(e);
      }
    });

    // Voted successfully - using general requestMessage
    /*if( typeof modalData !== 'undefined' && typeof modalData.voteSuccess !== 'undefined' ){
        generateModal(modalData.voteSuccess(), 'minimal');
      }*/

    // Confirm send to VRK
    $('.send-to-vrk-confirm').click(function () {
      try {
        generateModal(modalData.sendToVRKConfirm(), 'minimal');
        return false;
      } catch (e) {
        myConsole.log(e);
      }
    });

    // Confirm remove support votes
    $('.remove-support-votes').click(function () {
      try {
        generateModal(modalData.removeSupportVotesConfirm(), 'minimal');
        return false;
      } catch (e) {
        myConsole.log(e);
      }
    });

    /**
     *
     * Datepicker: jQuery tool - Dateinput
     * ===================================
     * - Uses the global Init.getLocale() variable to determine the localization
     *
     * */
    $.tools.dateinput.localize("fi", {
      months: 'Tammikuu,Helmikuu,Maaliskuu,Huhtikuu, Toukokuu, Kesäkuu, Heinäkuu, Elokuu, Syyskuu, Lokakuu, Marraskuu, Joulukuu',
      shortMonths: 'Tammi, Helmi, Maalis, Huhti, Touko, Kesä, Heinä, Elo, Syys, Loka, Marras, Joulu',
      days: 'Sunnuntai, Maanantai, Tiistai, Keskiviikko, Torstai, Perjantai, Lauantai',
      shortDays: 'Su, Ma, Ti, Ke, To, Pe, La'
    });
    $.tools.dateinput.localize("sv", {
      months: 'Januari, Februari, Mars, April, Maj, Juni, Juli, Augusti, September, Oktober, November, December',
      shortMonths: 'Jan, Feb, Mar, Apr, Maj, Jun, Jul, Aug, Sep, Okt, Nov, Dec',
      days: 'Söndag, Måndag, Tisdag, Onsdag, Torsdag, Lördag',
      shortDays: 'Sö, Må, Ti, On, To, Fr, Lö'
    });

    $.tools.dateinput.conf.lang = Init.getLocale();

    $(".datepicker").dateinput({
      format: Init.getDateFormat(), // this is displayed to the user
      firstDay: 1, // First day is monday
      offset: [0, 0],

      // a different format is sent to the server
      change: function () {
        var isoDate = this.getValue('yyyy-mm-dd');
        $("#backendValue").val(isoDate);
      }
    }).blur(function () {
      // Trim spaces in copy pasted value
      var trimmed = $.trim($(this).val());
      $(this).val(trimmed);
    });

    /**
     *
     * Tooltip: jQuery tools - Tooltip
     * ===============================
     *
     * */
    $('.trigger-tooltip[title]').tooltip({
      animation: true,
      effect: 'fade',
      placement: 'top right',
      offset: [-6, 0],
      trigger: 'hover',
      predelay: 500 // Small delay before showing tooltip for more relaxed behaviour.
    }).dynamic({
      left: {
        direction: 'right'
      },
      right: {
        direction: 'left'
      },
      top: {
        direction: 'bottom'
      }
    });

    /**
     * DirtyForms jQuery plugin
     * ========================
     * http://mal.co.nz/code/jquery-dirty-forms/
     *
     * - Checks if form is modified and fires up a notification
     * - To improve usage, LiveQuery could be used
     *   http://brandonaaron.net/code/livequery/docs
     * - $.DirtyForms.dialog overrides plugin's default facebox-dialog
     *
     * */

    $.DirtyForms.dialog = {
      ignoreAnchorSelector: 'a[rel="external"], .initiative-modal a', // Ignore external links

      // Selector is a selector string for dialog content. Used to determine if event targets are inside a dialog
      selector: '.initiative-modal .modal-content',

      // Fire starts the dialog
      fire: function (message, title) {
        try {
          generateModal(modalData.formModifiedNotification(), "minimal");
        } catch (e) {
          myConsole.log(e);
        }
      },
      // Bind binds the continue and cancel functions to the correct links
      bind: function () {
        $('.initiative-modal .close').click(function (e) {
          $.DirtyForms.choiceContinue = false;
          $.DirtyForms.choiceCommit(e);
        });
        $('.initiative-modal .continue').click(function (e) {
          $.DirtyForms.choiceContinue = true;
          $.DirtyForms.choiceCommit(e);
        });

        $(document).bind('decidingcancelled.dirtyforms', function () {
          $(document).trigger('close.facebox');
        });
      },

      // Refire handles closing an existing dialog AND fires a new one
      refire: function (content) {
        return false;
      },

      // Stash returns the current contents of a dialog to be refired after the confirmation
      // Use to store the current dialog, when it's about to be replaced with the confirmation dialog.
      // This function can return false if you don't wish to stash anything.
      stash: function () {
        return false;
      }
    };

    // Listen forms that have class 'sodirty'
    $('form.sodirty').dirtyForms();


    /**
     * Toggle financial URL
     * ====================
     * - Toggles an element depending on the selection of other element (radiobutton or checkbox)
     * - If the input is clicked hidden:
     *      * the input is disabled so that value will not be saved
     *      * the value is not removed so that the value can be retrieved
     *        when clicked back to visible
     */
    (function () {
      var financeUrlArea, $financeAreaLabel, idHasSupport, $financialSupportURL, toggleFinancialUrl;

      financeUrlArea = '.initiative-finance-url-area';
      $financeAreaLabel = $('.initiative-finance-area label');
      idHasSupport = 'initiative.financialSupport.true';
      $financialSupportURL = $('#financialSupportURL');

      toggleFinancialUrl = function (clicker, input) {
        if (input.attr('id') === idHasSupport) {
          clicker.siblings(financeUrlArea).slideDown(speedVeryFast);
          $financialSupportURL.removeAttr('disabled');
        } else {
          clicker.siblings(financeUrlArea).slideUp(speedVeryFast);
          $financialSupportURL.attr('disabled', 'disabled');
        }
      };

      $financeAreaLabel.each(function () {
        var clicker, input;
        clicker = $(this);
        input = clicker.find("input:first");

        if (input.is(':checked') && input.attr('id') === idHasSupport) {
          $financialSupportURL.removeAttr('disabled');
          $(financeUrlArea).show();
        }

        clicker.click(function () {
          if (clicker.children('input[type="radio"]').length > 0) {
            toggleFinancialUrl($(this), input);
          }
        });

      });
    }());

    /**
     * Toggle external support votes
     * =============================
     */
    (function () {
      var $supportNotificationLabel, $externalSupportCount, $externalSupportCountField,
        $supportStatementPdf, $supportStatementsInWeb, $supportStatementsOnPaper;

      $supportNotificationLabel = $('.initiative-support-notifications-area label');
      $externalSupportCount = $('.initiative-external-support-count');
      $supportStatementPdf = $('.initiative-support-statement-pdf');
      $externalSupportCountField = $('#externalSupportCount');
      $supportStatementsInWeb = $("#supportStatementsInWeb");
      $supportStatementsOnPaper = $("#supportStatementsOnPaper");

      // Replace empty val with 0
      $externalSupportCountField.blur(function () {
        var input = $(this);
        if (input.val() === "") {
          input.val(0);
        }
      });

      $supportNotificationLabel.each(function () {
        var clicker, input, toggleField, showField;
        clicker = $(this);
        input = clicker.find("input:first");

        toggleField = function () {
          if ($supportStatementsInWeb.is(':checked') || $supportStatementsOnPaper.is(':checked')) {
            $externalSupportCountField.removeAttr('disabled');
            showField($externalSupportCount, true);
          } else {
            $externalSupportCountField.attr('disabled', 'disabled');
            showField($externalSupportCount, false);
          }

          if ($supportStatementsOnPaper.is(':checked')) {
            showField($supportStatementPdf, true);
          } else {
            showField($supportStatementPdf, false);
          }
        };

        showField = function (elem, show) {
          // Animation causes issues in input field's visibility with IE7
          if (show) {
            if (isIE7) {
              elem.show();
            } else {
              elem.stop(true, true).slideDown(speedVeryFast);
            }
          } else {
            if (isIE7) {
              elem.hide();
            } else {
              elem.stop(true, true).slideUp(speedVeryFast);
            }
          }
        };

        toggleField();

        clicker.click(function () {
          toggleField();
        });
      });
    }());


    /**
     * Handle VEV selection
     * ====================
     * - Gets saved emails from textarea and adds them to a list element
     * - Parses and validates typed emails and adds them to a list element.
     */
    (function () {
      $('form .email-field').each(function () {
        var field, newIndex, input, inputBox, name, parseEmails, parseCurrentEmails, addEmail, removeEmail,
          ul, li, unifyHeights,
          sentInvitations, getSentInvitations,
          sentInvitationsUl, sentInvitationsLi, emailPlaceHolder;

        field = $(this);
        newIndex = field.children(".invitation-data:first").data("index");
        name = field.data('name');
        sentInvitations = field.data('sent-invitations');
        ul = $('<ul class="emails no-style" />');
        inputBox = $('<div class="email-input" />');
        input = $('<textarea rows="1" />');
        sentInvitationsUl = $('<ul class="sent-invitations no-style" />');
        sentInvitationsLi = $('<li class="disabled" />');
        emailPlaceHolder = $('<span class="email-place-holder">malli@esimerkki.fi</span>');


        // Vertical align elements
        unifyHeights = function () {
          var fields, h;
          h = 0;
          fields = field.siblings('.email-field').add(field);
          fields.each(function () {
            var ip;
            ip = $(this).find('.email-input');
            ip.css('height', 'auto');
            if (ip.height() > h) {
              h = ip.height();
              return h;
            }
          });

          return fields.each(function () {
            return $(this).find('.email-input').height(h);
          });
        };

        // Remove email from list and empty corresponding input-field
        removeEmail = function (x, id) {
          $('input[name="' + id + '"]').val('');
          x.parent().remove();

          if ($('.email-input').find('.invalid').length === 0) {
            var authorArea = $('.initiative-authors-area');

            authorArea.find('.msg-error').fadeOut(speedSlow, function () {
              $(this).remove();
            });
          }
        };

        // Add a list element
        addEmail = function (x, email, index, addSaved, invalid) {
          var invitationName, hid;

          if (addSaved === null) {
            addSaved = false;
          }
          if (invalid === null) {
            invalid = false;
          }

          invitationName = name + '[' + index + '].email';
          li = $('<li />').text(email).attr('id', invitationName);

          if (invalid) {
            li.addClass('invalid');
          }

          x = $('<a href="#" class="remove">x</a>');

          // Don't add existing emails
          if (addSaved && !invalid) {
            hid = $('<input type="hidden" />').attr('name', invitationName).val(email);

            field.prepend(hid);
          }

          x.click(function () {
            removeEmail(x, invitationName);
            return false;
          });

          li.append(x);
          ul.append(li);
        };

        // Parse and add current emails in the UL-list
        parseCurrentEmails = function () {

          $.each(field.children('input[type="text"]'), function (index) {
            var x, email;
            email = $(this).val();

            if (validateEmail(email)) {
              addEmail(x, email, index);
              return unifyHeights();
            }

          });
        };

        // Parse and add typed emails in the UL-list
        parseEmails = function (e, blur) {
          var emails, val;
          if (e === null) {
            e = null;
          }
          if (blur === null) {
            blur = false;
          }
          val = input.val();
          emails = val.split(/[\s\n\t\v,;]+/);

          if (emails.length > 1 || blur) {
            $.each(emails, function (k, email) {
              var hid, x;
              email = $.trim(email);
              newIndex += 1;

              if (validateEmail(email)) {
                addEmail(x, email, newIndex, true);
                emails.splice(k, 1);
                return unifyHeights();
              } else if (email !== "" && blur) {
                addEmail(x, email, newIndex, true, true);
                emails.splice(k, 1);
                return unifyHeights();
              }
            });

            return input.val($.trim(emails.join(' ')));
          }
        };

        input.keydown(function (e) {
          if ((input.val()).length === 0 && e.keyCode === 8) {
            var lastLi = ul.find('li').last();
            lastLi.remove();
            removeEmail(lastLi.find('.remove'), lastLi.attr('id'));
          }
          if (e.keyCode === 13) {
            return parseEmails(e);
          }
        });

        input.keyup(function (e) {
          return parseEmails(e);
        });

        input.focusout(function (e) {
          return parseEmails(e, true);
        });


        input.blur(function (e) {
          return parseEmails(e, true);
        });

        parseCurrentEmails();

        inputBox.append(ul);
        inputBox.append(input);
        inputBox.append(emailPlaceHolder);
        if (ul.children().length > 0) {
          emailPlaceHolder.hide();
        }

        inputBox.click(function () {
          return input.focus();
        });
        input.focus(function () {
          emailPlaceHolder.stop(false, true).fadeOut(speedFast);
          $(this).parent().addClass("focus");
        });
        inputBox.focusout(function (e) {
          if (ul.children().length === 0) {
            emailPlaceHolder.stop(false, true).fadeIn(speedFast);
          }
          $(this).removeClass("focus");
        });

        field.append(inputBox);

      });
    }());

    /**
     * Validate VEV roles
     * ==================
     * Disables selections that cannot be submitted.
     * - User must be one of these: initiator, representative or reserve
     * - But he/she cannot be representative and reserve at the same time
     */
    (function () {
      $('.validate-roles').each(function () {
        var container, $field, initiator, representative, reserve,
          validateRoles, disableFields, enableFields;

        container = $(this);
        $field = container.find('input[type=checkbox]');

        // Match inputs with roles.
        $field.each(function () {
          var input = $(this);

          switch (input.attr('id').split('.').pop()) {
          case 'reserve':
            reserve = input;
            break;
          case 'representative':
            representative = input;
            break;
          default:
            initiator = input;
          }
        });

        // Enables/disables role checkboxes
        // Shows/hides user's name in organizer invitations block
        validateRoles = function () {
          $('ul li.user-role').hide();

          if (initiator.is(':checked')) {
            $('#user-role-initiator').show();
          }
          if (representative.is(':checked')) {
            disableFields(reserve);
            $('#user-role-representative').show();
          } else {
            enableFields(reserve);
          }

          if (reserve.is(':checked')) {
            disableFields(representative);
            $('#user-role-reserve').show();
          } else {
            enableFields(representative);
          }
        };

        disableFields = function (elem) {
          elem.attr('disabled', 'disabled');
          elem.closest('label').addClass('disabled-input');
        };

        enableFields = function (elem) {
          elem.removeAttr('disabled');
          elem.closest('label').removeClass('disabled-input');
        };

        if ((representative.is(':checked') && reserve.is(':checked'))) {
          $.each([representative, reserve], function () {
            $(this).closest('label').addClass('has-error');
          });
        } else {
          validateRoles();
        }

        $field.click(function () {
          validateRoles();
          $field.closest('label').removeClass('has-error');
        });

      });
    }());


    /**
     * Bind checkbox
     * =============
     * - Binds checkbox '.binder' with submit button '.bind'.
     * - Button is enabled when checkbox is checked otherwise disabled
     */
    jQuery.fn.bindCheckbox = function () {
      var cb, btn, cbVal;

      cb = $(this);
      btn = cb.parents('form').find('.bind');
      cbVal = function () {
        if (cb.is(':checked')) {
          btn.removeAttr('disabled').removeClass('disabled');
        } else {
          btn.attr('disabled', 'disabled').addClass('disabled');
        }
      };

      cbVal();
      cb.change(function () {
        cbVal();
      });
    };
    $('.binder').bindCheckbox();


    /**
     *
     * Show hide more details
     * ======================
     *
     * */
    $('.toggler').click(function () {
      var toggler = $(this),
        toggled = toggler.parent().next('.toggled');

      toggler.find('span').toggleClass('less');
      toggler.next('h4').toggleClass('subtle'); // relies on h4
      toggler.switchContent(toggler.find('.text:first'));

      if (isIE7) {
        toggled.stop(false, true).toggle(); // content disappeared in ie7 after animation
      } else {
        toggled.stop(false, true).slideToggle({
          duration: speedFast,
          easing: 'easeOutExpo'
        });
      }

      return false;
    });

    /**
     * Toggle block
     * ===========
     *
     * Quite similar to 'Show hide more details'.
     */
    (function () {
      var btnHolder = $('.js-open-block'),
        btn = $('.js-btn-open-block'),
        block = $('.js-block-container, .js-block-container-alt'),
        btnClose = block.find('.js-btn-close-block');

      btn.click(function (e) {
        e.preventDefault();

        var elemClass = $(this).data('open-block');

        btnHolder.hide();
        $('.' + elemClass).fadeIn(speedFast);

        //return false;
      });

      btnClose.click(function (e) {
        e.preventDefault();

        block.hide();
        btnHolder.fadeIn(speedFast);

        //return false;
      });

    }());


    /**
     * Refresh initiative support count
     * ================================
     *
     * JSON url is defined in layout.ftl
     *
     */
    (function () {
      var supportCount = function () {
        var loaderClass, loader, updateData, getData;

        loaderClass = 'support-count-loader';
        loader = '<span class="loader ' + loaderClass + '" />';

        updateData = function (target, count) {
          // Add spaces to separate thousands
          target.find('.vote-count').text(count.toString().replace(/\B(?=(\d{3})+(?!\d))/g, " "));

          setTimeout(function () {
            $('.' + loaderClass).remove();
          }, 300);
        };

        getData = function (initiativeId, clicker) {
          var targetTotal = $('#' + clicker.data('target-total')),
            targetInternal = $('#' + clicker.data('target-internal'));

          targetTotal.append(loader);
          targetInternal.append(loader);

          $.getJSON(Init.getSupportCountJson() + initiativeId).done(function (data) {
            updateData(targetTotal, data.totalSupportCount);
            updateData(targetInternal, data.supportCount);
          }).fail(function (e) {
            myConsole.log("Error loading data: " + e);
            $('.' + loaderClass).remove();
          });
        };

        return {
          refresh: function (initiativeId, clicker) {
            clicker.addClass('js-disabled');
            setTimeout(function () {
              clicker.removeClass('js-disabled');
            }, 5000); // disable the button for a moment to slow down multiple clicking

            getData(initiativeId, clicker);
          }
        };
      };

      $('.js-update-support-count').click(function () {
        var btn = $(this);

        if (!btn.hasClass('js-disabled')) {
          supportCount().refresh(btn.data('id'), btn);
        }
      });

    }());

    /**
    * Generate iFrame
    * ==================
    */
    if (window.hasIFrame) {

      (function () {
        var reset =         $('.js-reset-iframe'),
          refresh =         $('.js-update-iframe'),
          iframeContainer = $("#iframe-container"),
          initiativeId =    $('#initiativeId'),
          lang =            $('input[name="language"]'),
          defaultLang =     $('input[name="language"][value="' + locale + '"]'),
          showTitle =       $('input[name="showTitle"]'),
          width =           $('#width'),
          height =          $('#height'),
          currentLang =     locale,
          bounds =          window.bounds,
          api =             window.defaultData.api,

          getInitiative = function (api, initiativeId, callback) {
            if (initiativeId !== '') {
              $.getJSON(api + '/' + initiativeId).done(function (data) {
                if (callback) {
                  callback(data);
                }
              }).fail(function (e) {
                if (callback) {
                  callback(null);
                }
                myConsole.log('Error loading data: ' + e.status + ' - ' + e.statusText);
              });
            } else {
              if (callback) {
                callback(null);
              }
            }
          },

          populateInitiativeDetails = function (params) {
            getInitiative(api, params.initiativeId, function (data) {
              var initiativeName = '';

              if (data) {
                initiativeName = data.name[params.lang] || data.name.fi || '';
              }

              $('#initiative-name').text(initiativeName);
            });
          },

          generateIframe = function (params) {
            populateInitiativeDetails(params);
            iframeContainer.html($("#iframe-template").render(params));
            return false;
          },

          checkBounds = function (elem) {
            var min, max, def,
              val = parseInt(elem.val(), 10);

            switch (elem.attr('id')) {
            case width.attr('id'):
              min = bounds.min.width;
              max = bounds.max.width;
              break;
            case height.attr('id'):
              min = bounds.min.height;
              max = bounds.max.height;
              break;
            default:
              // nop
            }

            if (!/^\d+$/.test(val)) {
              elem.val(min); // set to min if not even a number
            }
            if (val < min) {
              elem.val(min);
            }
            if (val > max) {

              elem.val(max);
            }
          },

          getParams = function () {
            return {
              showPreview: initiativeId.val() !== '' && initiativeId.val() !== '0',
              initiativeId: initiativeId.val(),
              lang:     $('input[name="language"]:checked').val(),
              showTitle: $('input[name="showTitle"]:checked').val() === 'on',
              width:      width.val(),
              height:     height.val()
            };
          },

          refreshFields = function (data) {
            initiativeId.val(data.initiativeId).trigger('liszt:updated');
            lang.removeAttr('checked');
            showTitle.removeAttr('checked');
            defaultLang.attr('checked', 'checked');
            width.val(data.width);
            height.val(data.height);
          };

        generateIframe(getParams());

        initiativeId.change(function () {
          generateIframe(getParams());
        });

        width.add(height).change(function () {
          delay(function () {
            checkBounds(width);
            checkBounds(height);
            generateIframe(getParams());
          }, 100);
        });

        showTitle.change(function () {
          generateIframe(getParams());
        });

        lang.change(function () {
          generateIframe(getParams());
        });

        reset.click(function (e) {
          e.preventDefault();

          if (window.defaultData) {
            $.extend(window.defaultData, {
              initiativeId: initiativeId.val()
            });

            refreshFields(window.defaultData);
            generateIframe(getParams());
          }
        });

        refresh.click(function (e) {
          e.preventDefault();

          generateIframe(getParams());
        });

      }());
    }
  });

}());