(function() {
	"use strict";
	var Effect = {
		canvas: null,
		context: null,
		canvas_bk: null,
		context_bk: null,
		parts: [],
		rand: function(min, max) { return Math.random() * (max - min) + min; },
		hsla: function(h, s, l, a) { return 'hsla(' + h + ',' + s + '%,' + l + '%,' + a + ')'; },
		create: function() {
			var w = window.innerWidth;
			var h = window.innerHeight;
			this.canvas_bk = document.createElement("canvas");
			this.canvas = document.createElement("canvas");
			this.canvas_bk.setAttribute("style", "position:absolute;top:0;left:0;z-index:-2;background:#000;");
			this.canvas.setAttribute("style", "position:absolute;top:0;left:0;z-index:-1;");
			document.body.appendChild(this.canvas_bk);
			document.body.appendChild(this.canvas);
			this.canvas_bk.width = w;
			this.canvas_bk.height = h;
			this.canvas.width = w;
			this.canvas.height = h;
			this.context_bk = this.canvas_bk.getContext("2d");
			this.context = this.canvas.getContext("2d");
			this.context_bk.clearRect(0, 0, w, h);
			this.context_bk.globalCompositeOperation = 'lighter';
			var count1 = Math.floor((w + h) * 0.3);
			var count2 = Math.floor((w + h) * 0.03);
			var hue = this.rand(0, 360);
			while (count1--) {
				var x = this.rand(0, w);
				var y = this.rand(0, h);
				var radius = this.rand(1, (w + h) * 0.04);
				var c_h = this.rand(hue, hue + 100);
				var c_s = this.rand(10, 70);
				var c_l = this.rand(20, 50);
				var c_a = this.rand(0.1, 0.5);
				this.context_bk.shadowColor = this.hsla(c_h, c_s, c_l, c_a);
				this.context_bk.shadowBlur = this.rand(10, (w + h) * 0.04);
				this.context_bk.beginPath();
				this.context_bk.arc(x, y, radius, 0, Math.PI * 2.0);
				this.context_bk.closePath();
				this.context_bk.fill();
			}
			this.parts = [];
			while (count2--) {
				this.parts.push({
					radius: this.rand(1, (w + h) * 0.03),
					x: this.rand(0, w),
					y: this.rand(0, h),
					angle: this.rand(0, Math.PI * 2.0),
					vel: this.rand(0.1, 0.5),
					tick: this.rand(0, 10000)
				});
			}
		},
		draw: function() {
			this.context.clearRect(0, 0, this.canvas.width, this.canvas.height);
			this.context.globalCompositeOperation = 'source-over';
			this.context.shadowBlur = 0;
			this.context.drawImage(this.canvas, 0, 0);
			this.context.globalCompositeOperation = 'lighter';
			this.context.shadowBlur = 15;
			this.context.shadowColor = '#fff';
			for (var i = 0; i < this.parts.length; i++) {
				var part = this.parts[i];
				part.x += Math.cos(part.angle) * part.vel;
				part.y += Math.sin(part.angle) * part.vel;
				part.angle += this.rand(-0.05, 0.05);
				this.context.beginPath();
				this.context.arc(part.x, part.y, part.radius, 0, Math.PI * 2.0);
				this.context.fillStyle = this.hsla(0, 0, 100, 0.075 + Math.cos(part.tick * 0.02) * 0.05);
				this.context.fill();
				if (part.x - part.radius > this.canvas.width) { part.x = -part.radius }
				if (part.x + part.radius < 0) { part.x = this.canvas.width + part.radius }
				if (part.y - part.radius > this.canvas.height) { part.y = -part.radius }
				if (part.y + part.radius < 0) { part.y = this.canvas.height + part.radius }
				part.tick++;
			}
		},
		animation: function() {
			window.requestAnimationFrame = (function() {
				return window.requestAnimationFrame || function(callback) {
					window.setTimeout(callback, 1000 / 60);
				};
			})();
			var renderLoop = function() {
				this.draw();
				window.requestAnimationFrame(renderLoop);
			}.bind(this);
			window.requestAnimationFrame(renderLoop);
		},
		run: function() {
			this.create();
			this.animation();
			window.addEventListener('resize', function() {
				var w = window.innerWidth;
				var h = window.innerHeight;
				this.canvas.width = w;
				this.canvas.height = h;
				this.canvas_bk.width = w;
				this.canvas_bk.height = h;
				this.create();
			}.bind(this), false);
		}
	};
	Effect.run();
})();