import $ from 'jquery';

var suggestion = (function () {
	var threads = 4;
	var workers = [];
	var textarea = $('#box');
	var resultBox = $('#result');
	var start;
	var interval = null;
	var isIntervalSet = false;
	var found = false;
	var result = '';
	var words = {
		0: [],
		1: [],
		2: [],
		3: []
	};

	var init = function () {
		prepareThreads();

		textarea.keyup(function () {
			if (!isIntervalSet) {
				isIntervalSet = true;
				found = false;
				interval = setInterval(function () {
					var text = textarea.val();
					var textArr = text.split(' ');

					if (textArr[textArr.length - 1] !== '') {
						start = Date.now();
						findResult(textArr[textArr.length - 1].toLowerCase());
					}
				}, 3000);
			}
		});

		resultBox.bind('DOMSubtreeModified', function () {
			clearInterval(interval);
			isIntervalSet = false;
		});
	};

	var hackFunctionToBuffer = function (f) {
		return URL.createObjectURL(new Blob([`(${f.toString()})()`], { type: 'application/javascript' }));
	};

	var prepareThreads = function () {
		$.ajax({
			url: 'slowa.txt',
			async: true,
			success: function (data) {
				var lines = data.split('\n');
				for (var i = 0, n = lines.length; i < n; ++i) {
					var line = lines[i].slice(0, -1);
					if (i % threads === 0) {
						words[0].push(line);
					} else if (i % threads === 1) {
						words[1].push(line);
					} else if (i % threads === 2) {
						words[2].push(line);
					} else if (i % threads === 3) {
						words[3].push(line);
					}
				}

				var workerBlob = hackFunctionToBuffer(workerFunction);

				for (var j = 0; j < threads; j++) {
					var worker = new Worker(workerBlob);
					workers.push(worker);

					// send just an array
					worker.postMessage({
						content: 'array',
						words: words[j]
					});
				}
			}
		});
	};

	var finish = function (word) {
		result = word;
		resultBox.html(result + ', ' + (Date.now() - start) / 1000);
	};

	var findResult = function (target) {
		for (var j = 0, n = workers.length; j < n; j++) {
			var worker = workers[j];

			worker.postMessage({
				content: 'target',
				word: target
			});

			// recv
			worker.onmessage = function (e) {
				var word = e.data.word;
				var distance = e.data.distance;

				if (!found) {
					if (distance === 0) {
						found = true;
						finish(word);
					} else if (distance === 1) {
						found = true;
						finish(word);
					}
				}
			};
		};
	};

	var workerFunction = function () {
		var words = [];
		/**
		 * @param {string} a
		 * @param {string} b
		 * @return {number}
		 */
		var levenshteinDistance = function (a, b) {
			if (!a || !b) {
				return;
			}
			// Create empty edit distance matrix for all possible modifications of
			// substrings of a to substrings of b.
			const distanceMatrix = Array(b.length + 1).fill(null).map(() => Array(a.length + 1).fill(null));

			// Fill the first row of the matrix.
			// If this is first row then we're transforming empty string to a.
			// In this case the number of transformations equals to size of a substring.
			for (var i = 0; i <= a.length; i += 1) {
				distanceMatrix[0][i] = i;
			}

			// Fill the first column of the matrix.
			// If this is first column then we're transforming empty string to b.
			// In this case the number of transformations equals to size of b substring.
			for (var j = 0; j <= b.length; j += 1) {
				distanceMatrix[j][0] = j;
			}

			for (var j = 1; j <= b.length; j += 1) {
				for (var i = 1; i <= a.length; i += 1) {
					const indicator = a[i - 1] === b[j - 1] ? 0 : 1;
					distanceMatrix[j][i] = Math.min(
						distanceMatrix[j][i - 1] + 1, // devarion
						distanceMatrix[j - 1][i] + 1, // insertion
						distanceMatrix[j - 1][i - 1] + indicator // substitution
					);
				}
			}
			return distanceMatrix[b.length][a.length];
		};

		this.onmessage = function (e) {
			var msg = e.data;
			switch (msg.content) {
				case 'array':
					words = msg.words;
					break;
				case 'target':
					var target = msg.word;
					var minimumDistance = 3;
					var word = '';
					if (words.indexOf(target) !== -1) {
						word = target;
						minimumDistance = 0;
					} else {
						for (var i = 0, n = words.length; i < n; i++) {
							var distance = levenshteinDistance(target, words[i]);
							if (distance < minimumDistance) {
								minimumDistance = distance;
								word = words[i];
							}
						}
					}
					if (word) {
						this.postMessage({
							word,
							distance: minimumDistance
						});
					}
					break;
			}
		};
	};

	return {
		init: init
	};
})();

export default suggestion;
