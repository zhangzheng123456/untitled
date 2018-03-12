/**
 * Created by Bizvane on 2017051905017/1/19.
 */
(function(d, f) {
    var s = d.document;
    var b = s.documentElement;
    var m = s.querySelector('meta[name="viewport"]');
    var n = s.querySelector('meta[name="flexible"]');
    var a = 0;
    var r = 0;
    var l;
    var e = f.flexible || (f.flexible = {});
    if (n) {
        var j = n.getAttribute("content");
        if (j) {
            var q = j.match(/initial\-dpr=([\d\.]+)/);
            var h = j.match(/maximum\-dpr=([\d\.]+)/);
            if (q) {
                a = parseFloat(q[1]);
                r = parseFloat((1 / a).toFixed(2))
            }
            if (h) {
                a = parseFloat(h[1]);
                r = parseFloat((1 / a).toFixed(2))
            }
        }
    }
    if (!a && !r) {
        var c = d.navigator.appVersion;
        var p = d.navigator.appVersion.match(/android/gi);
        var o = d.navigator.appVersion.match(/iphone/gi);
        var k = d.devicePixelRatio;
        if (o) {
            if (c.match(/OS 9_3/) || c.match(/OS [0-7]_[0-9]/gi) || c.match(/OS 8_[0-3]/gi)) {
                a = 1
            } else {
                if (k >= 3 && (!a || a >= 3)) {
                    a = 1
                } else {
                    if (k >= 2 && (!a || a >= 2)) {
                        a = 1
                    } else {
                        a = 1
                    }
                }
            }
        } else {
            a = 1
        }
        r = 1 / a
    }
    b.setAttribute("data-dpr", a);
    if (!m) {
        m = s.createElement("meta");
        m.setAttribute("name", "viewport");
        if (b.firstElementChild) {
            b.firstElementChild.appendChild(m)
        } else {
            var g = s.createElement("div");
            g.appendChild(m);
            s.write(g.innerHTML)
        }
    }
    m.setAttribute("content", "width=device-width,initial-scale=" + r + ", maximum-scale=" + r + ", minimum-scale=" + r + ", user-scalable=no");

    function i() {
        var t = b.getBoundingClientRect().width;
        if (t / a > 540) {
            t = 540 * a
        }
        var u = t / 10;
        b.style.fontSize = u + "px";
        e.rem = d.rem = u
    }
    d.addEventListener("resize", function() {
        clearTimeout(l);
        l = setTimeout(i, 300)
    }, false);
    d.addEventListener("pageshow", function(t) {
        if (t.persisted) {
            clearTimeout(l);
            l = setTimeout(i, 300)
        }
    }, false);
    if (s.readyState === "complete") {
        s.body.style.fontSize = 12 * a + "px"
    } else {
        s.addEventListener("DOMContentLoaded", function(t) {
            s.body.style.fontSize = 12 * a + "px"
        }, false)
    }
    i();
    e.dpr = d.dpr = a;
    e.refreshRem = i;
    e.rem2px = function(u) {
        var t = parseFloat(u) * this.rem;
        if (typeof u === "string" && u.match(/rem$/)) {
            t += "px"
        }
        return t
    };
    e.px2rem = function(u) {
        var t = parseFloat(u) / this.rem;
        if (typeof u === "string" && u.match(/px$/)) {
            t += "rem"
        }
        return t
    }
})(window, window["lib"] || (window["lib"] = {}));