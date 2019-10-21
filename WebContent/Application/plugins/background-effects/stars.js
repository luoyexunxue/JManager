(function() {
	"use strict";
	var Transform = {
		camera: {
			viewport: { x: 0, y: 0 },
			obj: { x: 0, y: 0, z: 150 },
			dist: { x: 0, y: 0, z: 0 },
			ang: { cplane: 0, splane: 0, stheta: 0, ctheta: 0 }
		},
		parts: {
			scale: function(p, value) {
				return { x: p.x * value.x, y: p.y * value.y, z: p.z * value.z };
			},
			rotate: function(p, value) {
				var sx = Math.sin(value.x);
				var cx = Math.cos(value.x);
				var sy = Math.sin(value.y);
				var cy = Math.cos(value.y);
				var sz = Math.sin(value.z);
				var cz = Math.cos(value.z);
				return {
					x: p.x * cy * cz + p.y * sx * sy * cz + p.z * cx * sy * cz - p.y * cx * sz + p.z * sx * sz,
					y: p.x * cy * sz + p.y * sx * sy * sz + p.z * cx * sy * sz + p.y * cx * cz - p.z * sx * cz,
					z: p.y * sx * cy + p.z * cx * cy - p.x * sy
				};
			},
			translate: function(p, value) {
				return { x: p.x + value.x, y: p.y + value.y, z: p.z + value.z };
			}
		},
		update: function(x, y) {
			this.camera.obj.x += (x - this.camera.obj.x) * 0.05;
			this.camera.obj.y += (y - this.camera.obj.y) * 0.05;
			this.camera.dist.x = -this.camera.obj.x;
			this.camera.dist.y = -this.camera.obj.y;
			this.camera.dist.z = -this.camera.obj.z;
			var dx2 = this.camera.dist.x * this.camera.dist.x;
			var dy2 = this.camera.dist.y * this.camera.dist.y;
			var dz2 = this.camera.dist.z * this.camera.dist.z;
			var xz = Math.sqrt(dx2 + dz2);
			var xyz = Math.sqrt(dx2 + dy2 + dz2);
			this.camera.ang.cplane = -this.camera.dist.z / xz;
			this.camera.ang.splane = this.camera.dist.x / xz;
			this.camera.ang.stheta = -this.camera.dist.y / xyz;
			this.camera.ang.ctheta = xz / xyz;
		},
		step: function(point, scale, rotate, translate) {
			var p = this.parts.scale(point, scale);
			var m = this.parts.rotate(p, rotate);
			var n = this.parts.translate(m, translate);
			m.y = n.y;
			m.x = n.x * this.camera.ang.cplane + n.z * this.camera.ang.splane;
			m.z = n.z * this.camera.ang.cplane - n.x * this.camera.ang.splane;
			n.x = m.x;
			n.y = m.y * this.camera.ang.ctheta - m.z * this.camera.ang.stheta;
			n.z = m.z * this.camera.ang.ctheta + m.y * this.camera.ang.stheta;
			m.x = n.x - this.camera.obj.x;
			m.y = n.y - this.camera.obj.y;
			m.z = n.z - this.camera.obj.z;
			return {
				x: this.camera.viewport.x + (m.x * this.camera.dist.z / m.z),
				y: this.camera.viewport.y - (m.y * this.camera.dist.z / m.z),
				r: Math.abs(this.camera.dist.z / m.z) * 3
			};
		}
	};

	var Sprite3D = function() {
		this.canvas = null;
		this.context = null;
		this.count = 200;
		this.velocity = 0.001;
		this.rotate = { x: 0, y: 0, z: 0 };
		this.scale = {};
		this.points = [];
		this.movement = [];
		this.mouseX = 0;
		this.mouseY = 0;
		this.target = { x: 0, y: 0 };
		this.init();
	};

	Sprite3D.prototype.init = function() {
		var w = window.innerWidth;
		var h = window.innerHeight;
		this.canvas = document.createElement("canvas");
		this.canvas.setAttribute("style", "position:absolute;top:0;left:0;z-index:-1;background:#1e1e1e;");
		document.body.appendChild(this.canvas);
		this.canvas.width = w;
		this.canvas.height = h;
		this.context = this.canvas.getContext("2d");
		this.context.globalCompositeOperation = 'lighter';
		this.scale = { x: w / 2, y: h / 2, z: w / 2 };
		Transform.camera.viewport = { x: w / 2, y: h / 2, z: 0 };
		for (var i = 0; i < this.count; i++) {
			this.points.push({
				x: Math.random() * 2.0 - 1.0,
				y: Math.random() * 2.0 - 1.0,
				z: Math.random() * 2.0 - 1.0
			});
			this.movement.push({
				x: Math.random() * Math.PI * 2.0,
				y: Math.random() * Math.PI * 2.0,
				z: Math.random() * Math.PI * 2.0
			});
		}
	};

	Sprite3D.prototype.draw = function() {
		Transform.update(this.mouseX, this.mouseY);
		this.rotate.x += 0.001;
		this.rotate.y += 0.001;
		this.rotate.z += 0.001;
		this.context.clearRect(0, 0, this.canvas.width, this.canvas.height);
		var PI2 = Math.PI * 2.0;
		for (var i = 0; i < this.points.length; i++) {
			this.movement[i].x += this.velocity;
			this.movement[i].y += this.velocity;
			this.movement[i].z += this.velocity;
			if (this.movement[i].x > PI2) this.movement[i].x = 0;
			if (this.movement[i].y > PI2) this.movement[i].y = 0;
			if (this.movement[i].z > PI2) this.movement[i].z = 0;
			var movement = {
				x: 200 * Math.cos(this.movement[i].x),
				y: 200 * Math.sin(this.movement[i].y),
				z: 200 * Math.sin(this.movement[i].z)
			}
			var v = Transform.step(this.points[i], this.scale, this.rotate, movement);
			var g = this.context.createRadialGradient(v.x, v.y, v.r, v.x, v.y, v.r * 2);
			g.addColorStop(0, 'hsla(255,255%,255%,1)');
			g.addColorStop(0.5, 'hsla(' + i + ',85%,40%,1.0)');
			g.addColorStop(1.0, 'hsla(' + i + ',85%,40%,0.5)');
			this.context.fillStyle = g;
			this.context.beginPath();
			this.context.arc(v.x, v.y, v.r * 2, 0, Math.PI * 2);
			this.context.fill();
			this.context.closePath();
		}
	};

	Sprite3D.prototype.animation = function() {
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
	};

	Sprite3D.prototype.run = function () {
		this.animation();
		window.addEventListener('mousemove', function(e) {
			this.mouseX = (e.clientX - this.canvas.width / 2) * -0.8;
			this.mouseY = (e.clientY - this.canvas.height / 2) * 0.8;
		}.bind(this));
		window.addEventListener('touchmove', function(e) {
			e.preventDefault();
			this.mouseX = (e.touches[0].clientX - this.canvas.width / 2) * -0.8;
			this.mouseY = (e.touches[0].clientY - this.canvas.height / 2) * 0.8;
		}.bind(this));
		window.addEventListener('resize', function() {
			var w = window.innerWidth;
			var h = window.innerHeight;
			this.canvas.width = w;
			this.canvas.height = h;
			this.scale = { x: w / 2, y: h / 2, z: w / 2 };
			Transform.camera.viewport = { x: w / 2, y: h / 2 };
		}.bind(this), false);
	};

	new Sprite3D().run();
})();