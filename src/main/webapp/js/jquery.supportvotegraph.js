/* global $, jQuery, Raphael, moment */

(function ($) {
  'use strict';

  var graph, initGraph, clearChart;
  var firstLoad = true;
  var defaults = {
    data : {},
    color: '#087480',
    colorHl: '#bc448e',
    width : 960,
    height : 250,
    leftgutter : 50,
    rightgutter : 30,
    bottomgutter : 20,
    topgutter : 20,
    cumulative : true,
    zoomed : false,
    max : 50000
  };

  var methods = {
    init : function(options) {
      var settings = $.extend(defaults, options);

      // Override data with random data for developing purposes
      // settings.data.votes = testDataGererator('2014-06-04', '2014-12-04', 25, 50);
      // settings.data.votes = testDataGererator('2014-06-04', '2014-12-04', 300, 100);
      // settings.data.votes = testDataGererator('2014-06-04', '2014-10-04', 25, 50);
      // settings.data.votes = testDataGererator('2014-06-04', '2014-10-04', 1, 10);
      // settings.data.votes = testDataGererator('2014-08-04', '2014-10-05', 1, 10);
      // settings.data.votes = testDataGererator('2014-06-04', '2014-12-04', 25, 50);
      // settings.data.votes = testDataGererator('2014-06-07', '2014-06-15', 25, 50);
      // settings.data.votes = testDataGererator('2014-06-07', '2014-06-20', 25, 50);
      // settings.data.votes = testDataGererator('2014-06-04', '2014-06-25', 25, 50);
      // settings.data.startDate = '2014-06-04';
      // settings.data.endDate = '2014-12-04';

      return this.each(function (index, element) {
        var r,
          btnCumul = $('<a>' + settings.data.lang.btnCumul + '</a>'),
          btnDaily = $('<a>' + settings.data.lang.btnDaily + '</a>'),
          buttonsLeft = $('<span class="switch-buttons" />'),
          buttons = $('<div class="graph-actions" />'),
          btnZoomIn = $('<a title="' + settings.data.lang.btnZoomIn + '" class="trigger-tooltip"><i class="icon zoom-in"></i></a>'),
          btnZoomOut = $('<a title="' + settings.data.lang.btnZoomOut + '" class="trigger-tooltip"><i class="icon zoom-out"></i></a>'),
          zoomHolder = $('<div class="graph-zoom" />');

        function refreshGraph() {
          if (graph !== undefined){
            clearChart(graph, element);
          }
          graph = initGraph(element, settings);
        }

        function showZoomHolder(show) {
          if (show) {
            zoomHolder.css('display', 'inline-block');
          } else {
            zoomHolder.css('display', 'none');
          }
        }

        refreshGraph();

        if (firstLoad){
          buttonsLeft.append(btnCumul);
          buttonsLeft.append(btnDaily);

          $(element)
          .before(buttonsLeft)
          .before(buttons);

          // Zoom
          zoomHolder.append(btnZoomIn);
          zoomHolder.append(btnZoomOut);
          buttons.prepend(zoomHolder);
          btnZoomOut.addClass('act');
        }
        firstLoad = false;

        if (settings.cumulative) {
          showZoomHolder(true);
          btnCumul.addClass('active');
        } else {
          btnDaily.addClass('active');
        }

        btnCumul.click(function () {
          if (!$(this).hasClass('active')) {
            $(this).addClass('active');
            btnDaily.removeClass('active');
            settings.cumulative = true;
            showZoomHolder(true);

            refreshGraph();
          }
        });

        btnDaily.click(function () {
          if (!$(this).hasClass('active')) {
            $(this).addClass('active');
            btnCumul.removeClass('active');
            settings.cumulative = false;
            showZoomHolder(false);

            refreshGraph();
          }
        });

        btnZoomIn.click(function () {
          if (!$(this).hasClass('act')) {
            $(this).addClass('act');
            btnZoomOut.removeClass('act');
            settings.zoomed = true;

            refreshGraph();
          }
        });

        btnZoomOut.click(function () {
          if (!$(this).hasClass('act')) {
            $(this).addClass('act');
            btnZoomIn.removeClass('act');
            settings.zoomed = false;

            refreshGraph();
          }
        });

      });
    }
  };

  $.fn.supportVoteGraph = function(methodOrOptions) {
    if ( methods[methodOrOptions] ) {
      return methods[ methodOrOptions ].apply( this, Array.prototype.slice.call( arguments, 1 ));
    } else if ( typeof methodOrOptions === 'object' || ! methodOrOptions ) {
      // Default to "init"
      return methods.init.apply( this, arguments );
    } else {
      $.error( 'Method ' +  methodOrOptions + ' does not exist on jQuery.supportVoteGraph' );
    }
  };

  Raphael.fn.drawGrid = function (x, y, w, h, wv, hv, color, opacity) {
    color = color || "#000";
    opacity = opacity || 0.5;
    var path = ["M", Math.round(x) + 0.5, Math.round(y) + 0.5, "L", Math.round(x + w) + 0.5, Math.round(y) + 0.5, Math.round(x + w) + 0.5, Math.round(y + h) + 0.5, Math.round(x) + 0.5, Math.round(y + h) + 0.5, Math.round(x) + 0.5, Math.round(y) + 0.5],
      rowHeight = h / hv,
      columnWidth = w / wv,
      i;
    for (i = 1; i < hv; i++) {
      path = path.concat(["M", Math.round(x) + 0.5, Math.round(y + i * rowHeight) + 0.5, "H", Math.round(x + w) + 0.5]);
    }
    for (i = 1; i < wv; i++) {
      path = path.concat(["M", Math.round(x + i * columnWidth) + 0.5, Math.round(y) + 0.5, "V", Math.round(y + h) + 0.5]);
    }
    return this.path(path.join(",")).attr({stroke: color, opacity: opacity});
  };

  function separateThousands(value) {
    return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ' ');
  }

  function iterateData(data) {
    var labels = [],
      rawData = [],
      value,
      firstDate = data.votes[0].d,
      lastDate = data.votes[data.votes.length - 1].d,
      curDate = moment(data.startDate),
      days = moment(lastDate).diff(data.startDate, 'days') + 1,
      votingDays = moment(data.endDate).diff(moment(data.startDate), 'days') + 1,
      i = 0,
      n = 0;

    for (i = 0; i < days; i++) {
      value = 0;

      if (curDate.diff(data.votes[n].d, 'days') === 0) {
        value = data.votes[n].n;
        n++;
      }

      labels.push(curDate.format('D.M.YYYY'));
      rawData.push(value);
      curDate = moment(curDate).add('days', 1);
    }

    return {
      labels : labels,
      data : rawData,
      votingDays : votingDays
    };
  }

  function getAnchors(p1x, p1y, p2x, p2y, p3x, p3y) {
    var l1, l2, a, b, alpha, dx1, dy1, dx2, dy2;

    l1 = (p2x - p1x) / 2;
    l2 = (p3x - p2x) / 2;
    a = Math.atan((p2x - p1x) / Math.abs(p2y - p1y));
    b = Math.atan((p3x - p2x) / Math.abs(p2y - p3y));
    a = p1y < p2y ? Math.PI - a : a;
    b = p3y < p2y ? Math.PI - b : b;

    alpha = Math.PI / 2 - ((a + b) % (Math.PI * 2)) / 2;
    dx1 = l1 * Math.sin(alpha + a);
    dy1 = l1 * Math.cos(alpha + a);
    dx2 = l2 * Math.sin(alpha + b);
    dy2 = l2 * Math.cos(alpha + b);

    return {
      x1: p2x - dx1,
      y1: p2y + dy1,
      x2: p2x + dx2,
      y2: p2y + dy2
    };
  }

  function getCumulativeData(data) {
    var i,
      res = [],
      c = 0;

    for (i = 0; i < data.length; i++) {
      c += data[i];
      res.push(c);
    }
    return res;
  }

  function getMax(data, settings) {
    var max = Math.max.apply(Math, data);
    if (settings.cumulative && !settings.zoomed) {
      return max < settings.max ? settings.max : max;
    } else {
      return max;
    }
  }

  function getBreakpoints(factors) {
    var i, j,
      res = [],
      // base = [1, 2.5, 5, 7.5];
      base = [1, 2, 3, 4, 5, 6, 7, 8, 9];

    for (i = 0; i < factors.length; i++) {
      for (j = 0; j < base.length; j++) {
        res.push(base[j] * factors[i]);
      }
    }

    return res;
  }

  function getScale(max, size) {
    var i,
      res = [],
      breakpoints = getBreakpoints([10, 100, 1000, 10000, 100000]),
      scaleMax = function (max) {
        var j;
        for (j = 0; j < breakpoints.length; j++) {
          if (breakpoints[j] >= max) {
            return breakpoints[j];
          }
        }
      };

    for (i = 0; i <= size; i++) {
      res.push(scaleMax(max) * (i / size));
    }

    return res;
  }

  function getXDays(settings, labels, min, max) {
    if (settings.cumulative && !settings.zoomed) {
      return max;
    } else {
      return labels <= min ? min : max;
    }
  }

  function getXLabels(data, xLabels, xDays, totalVotingDays, dateFormat) {
    var i,
      res = [],
      firstDate = moment(data.startDate).diff(data.votes[0].d, 'days') < 0 ? data.startDate : data.votes[0].d,
      lastDate = data.votes[data.votes.length - 1].d,
      curDate = firstDate,
      diff = xDays / (xLabels - 1);

    if (diff < 1) {
      diff = 1;
    }

    for (i = 0; i < xLabels; i++) {
      if (i < xLabels - 1 || xDays < totalVotingDays) {
        curDate = moment(firstDate).add('days', Math.round(i * diff));
      } else {
        curDate = moment(data.endDate); // Use the last date, rounding might cause +/- 1 day difference
      }
      res.push(curDate.format(dateFormat));
    }

    return res;
  }

  function fitToSelectedView(settings, rawData, labels, xDays) {
    var data, dX, Y, max, scale;

    if (settings.cumulative) {
      data = getCumulativeData(rawData);
    } else {
      data = rawData;
    }

    dX = (settings.width - settings.leftgutter) / xDays;
    max = getMax(data, settings);
    scale = getScale(max, 10);
    Y = (settings.height - settings.bottomgutter - settings.topgutter) / scale[scale.length - 1];

    return {
      data: data,
      max : max,
      scale : scale,
      dX: dX,
      Y: Y
    };
  }

  function testDataGererator(firstDate, lastDate, daily, tolerance) {
    var i, data = [],
      days = moment(lastDate).diff(firstDate, 'days') + 1,
      curDate = firstDate,
      rnd;

    for (i = 0; i < days; i++) {
      rnd = Math.floor((Math.random() - 0.5) * 2 * tolerance + daily);

      data.push({
        d: moment(curDate).format('YYYY-MM-DD'),
        n: rnd > 0 ? rnd : 0
      });
      curDate = moment(curDate).add('days', 1);
    }

    return data;
  }

  initGraph = function (element, settings) {
    var labels = [],
      rawData = [],
      votingDays = 0,
      iteratedData = iterateData(settings.data);

    labels = iteratedData.labels;
    rawData = iteratedData.data;
    votingDays = iteratedData.votingDays;

    // Draw
    var width = settings.width,
      height = settings.height,
      leftgutter = settings.leftgutter,
      rightgutter = settings.rightgutter,
      bottomgutter = settings.bottomgutter,
      topgutter = settings.topgutter,
      color = settings.color,
      colorHl = settings.colorHl,
      r = new Raphael(element, width, height),
      fontFamily = '"PT Sans", "Trebuchet MS", Helvetica, sans-serif',
      txt = {font: '12px ' + fontFamily, fill: '#fff'},
      txt1 = {font: '10px ' + fontFamily, fill: '#fff'},
      txtLabel = {font: '12px ' + fontFamily, fill: '#000'},
      txtLabelRight = {font: '12px ' + fontFamily, fill: '#000', 'text-anchor': 'end'},
      txtLabelLeft = {font: '12px ' + fontFamily, fill: '#000', 'text-anchor': 'start'},
      dateFormat = settings.width < 500 ? (settings.width < 400 ? 'D.M.' : 'D.M.YY') : 'D.M.YYYY',
      xDays = getXDays(settings, labels.length, 30, votingDays),
      fitted = fitToSelectedView(settings, rawData, labels, xDays),
      data = fitted.data,
      X = fitted.dX,
      max = fitted.max,
      scale = fitted.scale,
      Y = fitted.Y,
      y50 = height - bottomgutter + 0.5 - Y * 50,
      y50000 = height - bottomgutter + 0.5 - Y * settings.max,
      xLabels = 7;

    // Background grid
    r.drawGrid(leftgutter + xLabels * 0.5 + 0.5, topgutter + 0.5, width - leftgutter - xLabels, height - topgutter - bottomgutter, 6, 10, '#000', 0.05);

    if (settings.cumulative) {
      // Diagonal line from 0 to 50 000
      if (scale[scale.length - 1] >= 50000) {
        r.path(["M", leftgutter + xLabels - 0.5, height - bottomgutter + 0.5, "L", width - 0.5, y50000 ]).attr({fill: '#333', opacity: 0.1, 'stroke-dasharray': '--.'});
      }

      // Horizontal line in 50 000
      r.path(["M", leftgutter + X - 0.5, y50000, "L", width - 1, y50000 ]).attr({stroke: colorHl, 'stroke-width': 1});
    }

    // Horizontal line in 50
    if (settings.cumulative && scale[scale.length - 1] <= 1000) {
      r.path(["M", leftgutter + xLabels / 2 - 0.5, y50, "L", width - xLabels / 2 + 0.5, y50 ]).attr({stroke: colorHl, 'stroke-width': 1, opacity: 0.5});
    }

    var path = r.path().attr({stroke: color, 'stroke-width': 1, 'stroke-linejoin': 'round'}),
      bgp = r.path().attr({stroke: 'none', opacity: 0.3, fill: color}),
      label = r.set(),
      lx = 0,
      ly = 0,
      is_label_visible = false,
      leave_timer,
      blanket = r.set();

    label.push(r.text(60, 12, rawData[0]).attr(txt));
    label.push(r.text(60, 27, labels[0]).attr(txt1).attr({fill: '#fff'}));
    label.hide();

    var frame = r.popup(100, 100, label, 'right').attr({fill: color, 'stroke-width': 0, 'fill-opacity': 1}).hide();
    var p, bgpp, p0, bgpp0, i, ii;

    // Draw Y labels
    for (i = 0, ii = 10; i <= ii; i++) {
      var rowHeight = (height - topgutter - bottomgutter) / 10;
      if (i % 2 === 0) {
        r.text(leftgutter, height + topgutter - bottomgutter - Math.round(topgutter + i * rowHeight), separateThousands(scale[i])).attr(txtLabelRight).toBack();
      }
    }

    // Draw X labels
    if (settings.cumulative && !settings.zoomed) {
      for (i = 0; i < 7; i++) {
        r.text(leftgutter + 2 + ((width - leftgutter - X) / 6) * i, height - 6, moment(settings.data.startDate).add('months', i).format(dateFormat)).attr((i < 6) ? (i === 0 ? txtLabelLeft : txtLabel) : txtLabelRight).toBack();
      }
    } else {
      var xAxisLabels = getXLabels(settings.data, xLabels, xDays, votingDays, dateFormat);
      for (i = 0; i < xAxisLabels.length; i++) {
        r.text(leftgutter + 2 + ((width - leftgutter - xLabels) / 6) * i, height - 6, xAxisLabels[i]).attr((i < 6) ? (i === 0 ? txtLabelLeft : txtLabel) : txtLabelRight).toBack();
      }
    }

    for (i = 0, ii = labels.length; i < ii; i++) {
      var y = Math.round(height - bottomgutter - Y * data[i] - 1),
        x = Math.round(leftgutter + X * i + xLabels * 0.5);

      if (!i) {
        p = ['M', x, y, 'C', x, y];
        p0 = ['M', x, height - bottomgutter, 'C', x, height - bottomgutter];
        bgpp = ['M', leftgutter + xLabels * 0.5, height - bottomgutter + 1, 'L', x, y, 'C', x, y];
        bgpp0 = ['M', leftgutter + xLabels * 0.5, height - bottomgutter, 'L', x, height - bottomgutter + 1, 'C', x, height - bottomgutter];
      }
      if (i && i < ii - 1) {
        var Y0 = Math.round(height - bottomgutter - Y * data[i - 1]),
          X0 = Math.round(leftgutter + X * (i - 0.5)),
          Y2 = Math.round(height - bottomgutter - Y * data[i + 1]),
          X2 = Math.round(leftgutter + X * (i + 1.5)),
          a = getAnchors(X0, Y0, x, y, X2, Y2);

        p = p.concat([a.x1, a.y1, x, y, a.x2, a.y2]);
        p0 = p0.concat([a.x1, height - bottomgutter, x, height - bottomgutter, a.x2, height - bottomgutter]);
        bgpp = bgpp.concat([a.x1, a.y1, x, y, a.x2, a.y2]);
        bgpp0 = bgpp0.concat([a.x1, height - bottomgutter, x, height - bottomgutter, a.x2, height - bottomgutter]);
      }

      // Put a marker for the first day when there is no line.
      if (ii === 1) {
        r.circle(x, y, 2).attr({fill: color, 'stroke-width': 0});
      }

      var dot = r.circle(x, y, 2).attr({fill: colorHl, stroke: color, 'stroke-width': 0, opacity: 0});
      var hoverLine = r.path(["M", x, topgutter + 0.5, "L", x, height - bottomgutter + 0.5 ]).attr({fill: '#333', opacity: 0});

      blanket.push(r.rect(leftgutter + X * i, 0, X, height - bottomgutter).attr({stroke: 'none', fill: '#fff', opacity: 0}));
      var rect = blanket[blanket.length - 1];

      (function (x, y, data, lbl, dot, hoverLine) {
        var timer, i = 0;
        rect.hover(function () {
          clearTimeout(leave_timer);
          var side = 'right';
          if (x + frame.getBBox().width > width) {
            side = 'left';
          }
          var ppp = r.popup(x, y, label, side, 1),
            anim = Raphael.animation({
              path: ppp.path,
              transform: ['t', ppp.dx, ppp.dy]
            }, 200 * is_label_visible);
          lx = label[0].transform()[0][1] + ppp.dx;
          ly = label[0].transform()[0][2] + ppp.dy;
          frame.show().stop().animate(anim);
          label[0].attr({text: separateThousands(data)}).show().stop().animateWith(frame, anim, {transform: ['t', lx + 15, ly + 5]}, 200 * is_label_visible);
          label[1].attr({text: lbl}).show().stop().animateWith(frame, anim, {transform: ['t', lx + 15, ly + 5]}, 200 * is_label_visible);
          dot.attr({opacity: 1});
          hoverLine.attr({opacity: 0.2});
          is_label_visible = true;
        }, function () {
          dot.attr({opacity: 0});
          hoverLine.attr({opacity: 0});
          leave_timer = setTimeout(function () {
            frame.hide();
            label[0].hide();
            label[1].hide();
            is_label_visible = false;
          }, 1);
        });
      }(x, y, data[i], labels[i], dot, hoverLine));
    }

    p = p.concat([x, y, x, y]);
    p0 = p0.concat([x, height - bottomgutter, x, height - bottomgutter]);
    bgpp = bgpp.concat([x, y, x, y, 'L', x, height - bottomgutter + 1, 'z']);
    bgpp0 = bgpp0.concat([x, height - bottomgutter, x, height - bottomgutter, 'L', x, height - bottomgutter, 'z']);

    path.attr({path: p0});
    bgp.attr({path: bgpp0});

    function animation(time) {
      var anim = Raphael.animation({path: p}, time, '>');

      path.animate(anim);
      bgp.animateWith(path, anim, {path: bgpp}, time, '>');
    }

    animation(300);

    frame.toFront();
    label[0].toFront();
    label[1].toFront();
    blanket.toFront();

    return r;
  };

  clearChart = function (r, el) {
    r.clear();
    $(el).html('');
  };
}(jQuery));

// Raphael popup
(function () {
  'use strict';

  var tokenRegex = /\{([^\}]+)\}/g,
    objNotationRegex = /(?:(?:^|\.)(.+?)(?=\[|\.|$|\()|\[('|')(.+?)\2\])(\(\))?/g, // matches .xxxxx or ['xxxxx'] to run over object properties
    replacer = function (all, key, obj) {
      var res = obj;
      key.replace(objNotationRegex, function (all, name, quote, quotedName, isFunc) {
        name = name || quotedName;
        if (res) {
          if (res.hasOwnProperty(name)) {
            res = res[name];
          }
          if (typeof res === 'function' && isFunc) {
            res = res();
          }
        }
      });
      res = (res === null || res === obj ? all : res).toString();
      return res;
    },
    fill = function (str, obj) {
      return String(str).replace(tokenRegex, function (all, key) {
        return replacer(all, key, obj);
      });
    };

  Raphael.fn.popup = function (X, Y, set, pos, ret) {
    pos = String(pos || 'top-middle').split('-');
    pos[1] = pos[1] || 'middle';
    var r = 1,
      bb = set.getBBox(),
      w = Math.round(bb.width) + 30,
      h = Math.round(bb.height) + 10,
      x = Math.round(bb.x) - r,
      y = Math.round(bb.y) - r,
      gap = Math.min(h / 2, w / 2, 10),
      shapes = {
        top: 'M{x},{y}h{w4},{w4},{w4},{w4}a{r},{r},0,0,1,{r},{r}v{h4},{h4},{h4},{h4}a{r},{r},0,0,1,-{r},{r}l-{right},0-{gap},{gap}-{gap}-{gap}-{left},0a{r},{r},0,0,1-{r}-{r}v-{h4}-{h4}-{h4}-{h4}a{r},{r},0,0,1,{r}-{r}z',
        bottom: 'M{x},{y}l{left},0,{gap}-{gap},{gap},{gap},{right},0a{r},{r},0,0,1,{r},{r}v{h4},{h4},{h4},{h4}a{r},{r},0,0,1,-{r},{r}h-{w4}-{w4}-{w4}-{w4}a{r},{r},0,0,1-{r}-{r}v-{h4}-{h4}-{h4}-{h4}a{r},{r},0,0,1,{r}-{r}z',
        right: 'M{x},{y}h{w4},{w4},{w4},{w4}a{r},{r},0,0,1,{r},{r}v{h4},{h4},{h4},{h4}a{r},{r},0,0,1,-{r},{r}h-{w4}-{w4}-{w4}-{w4}a{r},{r},0,0,1-{r}-{r}l0-{bottom}-{gap}-{gap},{gap}-{gap},0-{top}a{r},{r},0,0,1,{r}-{r}z',
        left: 'M{x},{y}h{w4},{w4},{w4},{w4}a{r},{r},0,0,1,{r},{r}l0,{top},{gap},{gap}-{gap},{gap},0,{bottom}a{r},{r},0,0,1,-{r},{r}h-{w4}-{w4}-{w4}-{w4}a{r},{r},0,0,1-{r}-{r}v-{h4}-{h4}-{h4}-{h4}a{r},{r},0,0,1,{r}-{r}z'
      },
      offset = {
        hx0: X - (x + r + w - gap * 2),
        hx1: X - (x + r + w / 2 - gap),
        hx2: X - (x + r + gap),
        vhy: Y - (y + r + h + r + gap),
        '^hy': Y - (y - gap)
      },
      mask = [{
        x: x + r,
        y: y,
        w: w,
        w4: w / 4,
        h4: h / 4,
        right: 0,
        left: w - gap * 2,
        bottom: 0,
        top: h - gap * 2,
        r: r,
        h: h,
        gap: gap
      }, {
        x: x + r,
        y: y,
        w: w,
        w4: w / 4,
        h4: h / 4,
        left: w / 2 - gap,
        right: w / 2 - gap,
        top: h / 2 - gap,
        bottom: h / 2 - gap,
        r: r,
        h: h,
        gap: gap
      }, {
        x: x + r,
        y: y,
        w: w,
        w4: w / 4,
        h4: h / 4,
        left: 0,
        right: w - gap * 2,
        top: 0,
        bottom: h - gap * 2,
        r: r,
        h: h,
        gap: gap
      }][pos[1] === 'middle' ? 1 : (pos[1] === 'top' || pos[1] === 'left') * 2],
      dx = 0,
      dy = 0,
      out = this.path(fill(shapes[pos[0]], mask)).insertBefore(set);

    switch (pos[0]) {
    case 'top':
      dx = X - (x + r + mask.left + gap);
      dy = Y - (y + r + h + r + gap);
      break;
    case 'bottom':
      dx = X - (x + r + mask.left + gap);
      dy = Y - (y - gap);
      break;
    case 'left':
      dx = X - (x + r + w + r + gap);
      dy = Y - (y + r + mask.top + gap);
      break;
    case 'right':
      dx = X - (x - gap);
      dy = Y - (y + r + mask.top + gap);
      break;
    }

    out.translate(dx, dy);

    if (ret) {
      ret = out.attr('path');
      out.remove();
      return {
        path: ret,
        dx: dx,
        dy: dy
      };
    }
    set.translate(dx, dy);

    return out;
  };

}());