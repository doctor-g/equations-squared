function Board(size) {
	this.init = function(size) {
		var array = new Array(size);
		for ( var i = 0; i < size; i++) {
			array[i] = new Array(size);
			for ( var j = 0; j < size; j++) {
				array[i][j] = $('<td class=\"blank cell\">&#x25A0</td>');
			}
		}
		return array;
	};

	this.array = this.init(size);

	this.horizontal = function(col, row, string) {
		this.clearHighlights();
		var charList = string.split("");
		for ( var i = 0; i < charList.length; i++) {
			this.array[row][col + i] = this.makeCell(charList[i]);
		}
		return this;
	};

	this.vertical = function(col, row, string) {
		this.clearHighlights();
		var charList = string.split("");
		for ( var i = 0; i < charList.length; i++) {
			this.array[row + i][col] = this.makeCell(charList[i]);
		}
		return this;
	};

	this.caption = function(caption) {
		this.captionMesg = caption;
		return this;
	};

	this.makeCell = function(c) {
		return $('<div class="highlight cell">' + this.clean(c) + '</div>');
	};

	this.clean = function(c) {
		switch (c) {
		case '*':
			return '&times;';
		case '/':
			return '&divide;';
		default:
			return c;
		}
	};

	this.clearHighlights = function() {
		for ( var i = 0; i < this.array.length; i++) {
			for ( var j = 0; j < this.array[i].length; j++) {
				$(this.array[i][j]).removeClass('highlight');
			}
		}
	};

	this.highlight = function(x1, y1, x2, y2) {
		this.clearHighlights();
		for ( var i = x1; i <= x2; i++) {
			for ( var j = y1; j <= y2; j++) {
				$(this.array[j][i]).addClass('highlight');
			}
		}
		return this;
	};
	
	this.highlightArray = function(a) {
		this.clearHighlights();
		for (var i=0; i<a.length; i+=2) {
			var x = a[i];
			var y = a[i+1];
			$(this.array[y][x].addClass('highlight'));
		}
		return this;
	};

	this.node = function() {
		var node = $('<div class="content">');
		for ( var i = 0; i < this.array.length; i++) {
			col = $('<div class="col"/>');
			$(node).append(col);
		}
		for ( var i = 0; i < this.array.length; i++) {
			row = $('<div class="row">');
			$(node).append(row);
			for ( var j = 0; j < this.array.length; j++) {
				$(row).append(this.array[i][j]);
			}
		}
		if (this.captionMesg != null) {
			var cap = $('<div class="caption">' + this.captionMesg + '</div>');
			node.append(cap);
		}
		return node;
	};

	this.toString = function() {
		var result = "";
		for ( var i = 0; i < this.array.length; i++) {
			for ( var j = 0; j < this.array[i].length; j++) {
				result += $(this.array[i][j]).text();
			}
			result += '\n';
		}
		return result;
	};
}