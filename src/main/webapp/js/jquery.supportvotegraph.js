/*global $, jQuery, Raphael, moment */

(function ($) {
  'use strict';

  var initGraph, clearChart;

  $.fn.supportVoteGraph = function (options) {
    var settings = $.extend({
      data : [],
      color: '#087480',
      width : 960,
      height : 250,
      leftgutter : 50,
      rightgutter : 30,
      bottomgutter : 20,
      topgutter : 20,
      cumulative : true,
      max : 50000
    }, options);

    return this.each(function (index, element) {
      var r,
        btnCumul = $('<a>Näytä kokonaiskertymä</a>'),
        btnDaily = $('<a>Näytä päivittäinen kertymä</a>'),
        buttons = $('<div class="graph-actions" />');

      function refreshGraph() {
        r = initGraph(element, settings);
      }

      refreshGraph();

      buttons.append(btnCumul);
      buttons.append(btnDaily);

      $(element).before(buttons);

      if (settings.cumulative) {
        btnCumul.addClass('act');
      } else {
        btnDaily.addClass('act');
      }

      btnCumul.click(function () {
        if (!$(this).hasClass('act')) {
          $(this).addClass('act');
          btnDaily.removeClass('act');
          settings.cumulative = true;

          clearChart(r, element);
          refreshGraph();
        }
      });

      btnDaily.click(function () {
        if (!$(this).hasClass('act')) {
          $(this).addClass('act');
          btnCumul.removeClass('act');
          settings.cumulative = false;

          clearChart(r, element);
          refreshGraph();
        }
      });
    });
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

  function formatNumber(value) {
    return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ' ');
  }

  function iterateData(data) {
    var labels = [],
      rawData = [],
      value,
      firstDate = data[0].d,
      lastDate = data[data.length - 1].d,
      curDate = moment(firstDate),
      days = moment(lastDate).diff(firstDate, 'days'),
      i = 0,
      n = 0;

    for (i = 0; i <= days; i++) {
      value = 0;

      if (curDate.diff(data[n].d, 'days') === 0) {
        value = data[n].n;
        n++;
      }

      labels.push(curDate.format('D.M.YYYY'));
      rawData.push(value);
      curDate = moment(curDate).add('days', 1);
    }

    return {
      labels : labels,
      data : rawData
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
      result = [],
      c = 0;

    for (i = 0; i < data.length; i++) {
      c += data[i];
      result.push(c);
    }
    return result;
  }

  function getMax(data, cumulative, maxSupportCount) {
    var max = Math.max.apply(Math, data);
    if (cumulative) {
      return max < maxSupportCount ? maxSupportCount : max;
    } else {
      return max;
    }
  }

  function getScale(max, size) {
    var i,
      array = [],
      breakpoints = [10, 100, 500, 1000, 5000, 10000, 50000, 100000, 150000, 200000, 300000, 500000],
      scaleMax = function (max) {
        var j;
        for (j = 0; j < breakpoints.length; j++) {
          if (breakpoints[j] >= max) {
            return breakpoints[j];
          }
        }
      };

    for (i = 0; i <= size; i++) {
      array.push(scaleMax(max) * (i / size));
    }

    return array;
  }

  initGraph = function (element, settings) {
    var labels = [],
      rawData = [],
      data = [],
      iteratedData = iterateData(settings.data);

    labels = iteratedData.labels;
    rawData = iteratedData.data;

    // var randomData = function () {
    //   var d = new Date();
    //   var curr_date = d.getDate();
    //   var curr_month = d.getMonth() + 1; //Months are zero based
    //   var curr_year = d.getFullYear();
    //   var date = curr_date + "." + curr_month + "." + curr_year;

    //   for (var i = 0; i < 180; i++) {
    //     var n = Math.floor(Math.random() * 1000);
    //     labels.push(date);
    //     rawData.push(n);
    //   }
    // };
    // randomData();

    if (settings.cumulative) {
      data = getCumulativeData(rawData);
    } else {
      data = rawData;
    }

    // Draw
    var width = settings.width,
      height = settings.height,
      leftgutter = settings.leftgutter,
      rightgutter = settings.rightgutter,
      bottomgutter = settings.bottomgutter,
      topgutter = settings.topgutter,
      colorhue = 0.6,
      // color = 'hsl(' + [colorhue, 0.5, 0.5] + ')',
      color = settings.color,
      r = new Raphael(element, width, height),
      fontFamily = '"PT Sans", "Trebuchet MS", Helvetica, sans-serif',
      txt = {font: '12px ' + fontFamily, fill: '#fff'},
      txt1 = {font: '10px ' + fontFamily, fill: '#fff'},
      txtLabel = {font: '12px ' + fontFamily, fill: '#000'},
      txtLabelY = {font: '12px ' + fontFamily, fill: '#000', 'text-anchor': 'end'},
      X = (width - leftgutter) / labels.length,
      max = getMax(data, settings.cumulative, settings.max),
      Y = (height - bottomgutter - topgutter) / max,
      scale = getScale(max, 10);

    Y = (height - bottomgutter - topgutter) / scale[scale.length - 1];

    r.drawGrid(leftgutter + X * 0.5 + 0.5, topgutter + 0.5, width - leftgutter - X, height - topgutter - bottomgutter, 6, 10, '#000', 0.05);

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

    // var frame = r.popup(100, 100, label, 'right').attr({fill: '#000', stroke: '#666', 'stroke-width': 2, 'fill-opacity': .7}).hide();
    var frame = r.popup(100, 100, label, 'right').attr({fill: color, 'stroke-width': 0, 'fill-opacity': 1}).hide();
    var p, bgpp, p0, bgpp0, i, ii;

    // Draw Y labels
    for (i = 0, ii = 10; i <= ii; i++) {
      var rowHeight = (height - topgutter - bottomgutter) / 10;
      if (i % 2 === 0) {
        r.text(leftgutter, height - Math.round(topgutter + i * rowHeight), formatNumber(scale[i])).attr(txtLabelY).toBack();
      }
    }

    for (i = 0, ii = labels.length; i < ii; i++) {
      var y = Math.round(height - bottomgutter - Y * data[i]),
        x = Math.round(leftgutter + X * (i + 0.5));

      if (i % Math.floor(labels.length / 7) === 0) {
      // if (i % Math.floor(labels.length / 7) === 0 || i === labels.length - 1) {
        r.text(x, height - 6, labels[i]).attr(txtLabel).toBack();
      }
      if (!i) {
        p = ['M', x, y, 'C', x, y];
        p0 = ['M', x, height - bottomgutter, 'C', x, height - bottomgutter];
        bgpp = ['M', leftgutter + X * 0.5, height - bottomgutter, 'L', x, y, 'C', x, y];
        bgpp0 = ['M', leftgutter + X * 0.5, height - bottomgutter, 'L', x, height - bottomgutter, 'C', x, height - bottomgutter];
      }
      if (i && i < ii - 1) {
        var Y0 = Math.round(height - bottomgutter - Y * data[i - 1]),
          X0 = Math.round(leftgutter + X * (i - 0.5)),
          Y2 = Math.round(height - bottomgutter - Y * data[i + 1]),
          X2 = Math.round(leftgutter + X * (i + 1.5));
        var a = getAnchors(X0, Y0, x, y, X2, Y2);
        p = p.concat([a.x1, a.y1, x, y, a.x2, a.y2]);
        p0 = p0.concat([a.x1, height - bottomgutter, x, height - bottomgutter, a.x2, height - bottomgutter]);
        bgpp = bgpp.concat([a.x1, a.y1, x, y, a.x2, a.y2]);
        bgpp0 = bgpp0.concat([a.x1, height - bottomgutter, x, height - bottomgutter, a.x2, height - bottomgutter]);
      }

      var dot = r.circle(x, y, 2).attr({fill: '#333', stroke: color, 'stroke-width': 0, opacity: 0});
      blanket.push(r.rect(leftgutter + X * i, 0, X, height - bottomgutter).attr({stroke: 'none', fill: '#fff', opacity: 0}));
      var rect = blanket[blanket.length - 1];

      (function (x, y, data, lbl, dot) {
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
          label[0].attr({text: formatNumber(data)}).show().stop().animateWith(frame, anim, {transform: ['t', lx + 15, ly + 5]}, 200 * is_label_visible);
          label[1].attr({text: lbl}).show().stop().animateWith(frame, anim, {transform: ['t', lx + 15, ly + 5]}, 200 * is_label_visible);
          dot.attr({opacity: 1});
          is_label_visible = true;
        }, function () {
          dot.attr({opacity: 0});
          leave_timer = setTimeout(function () {
            frame.hide();
            label[0].hide();
            label[1].hide();
            is_label_visible = false;
          }, 1);
        });
      }(x, y, data[i], labels[i], dot));
    }

    p = p.concat([x, y, x, y]);
    p0 = p0.concat([x, height - bottomgutter, x, height - bottomgutter]);
    bgpp = bgpp.concat([x, y, x, y, 'L', x, height - bottomgutter, 'z']);
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