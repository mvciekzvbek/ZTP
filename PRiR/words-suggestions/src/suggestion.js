import $ from 'jquery';

var suggestion = (function () {
	var threads = 4;
	var words = {
		0: [],
		1: [],
		2: [],
		3: []
	};

	var textarea = $('#box');
	var resultBox = $('#result');

	var init = function () {
		prepareThreads(4);

		textarea.keyup(function () {
			var typed = this.value.split(' ');

			var target = (typed[typed.length - 1] !== '' || typed[typed.length - 1].length < 2 ? typed[typed.length - 1] : typed[typed.length - 2]);

			findResult(target);
		});
	};

	var hackFunctionToBuffer = function (f) {
		return URL.createObjectURL(new Blob([`(${f.toString()})()`], { type: 'application/javascript' }));
	};

	var prepareThreads = function (threads) {
		$.ajax({
			url: 'slowa.txt',
			async: true,
			success: function (data) {
				var lines = data.split('\n');
				for (var i = 0, n = data.length; i < n; ++i) {
					if (i % threads === 0) {
						words[0].push(lines[i]);
					} else if (i % threads === 1) {
						words[1].push(lines[i]);
					} else if (i % threads === 2) {
						words[2].push(lines[i]);
					} else if (i % threads === 3) {
						words[3].push(lines[i]);
					}
				}
			}
		});
	};

	var findResult = function (target) {
		var workerBlob = hackFunctionToBuffer(workerFunction);
		var result = [];

		for (let j = 0; j < threads; j++) {
			var worker = new Worker(workerBlob);

			// recv
			worker.onmessage = function (message) {
				result.push(message.data);
				resultBox.html(result[0]);
				this.terminate();
			};

			// send 
			worker.postMessage(
				{
					words: words[j],
					target: target
				}
			);
		}
	};

	var workerFunction = function () {
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
			for (let i = 0; i <= a.length; i += 1) {
				distanceMatrix[0][i] = i;
			}

			// Fill the first column of the matrix.
			// If this is first column then we're transforming empty string to b.
			// In this case the number of transformations equals to size of b substring.
			for (let j = 0; j <= b.length; j += 1) {
				distanceMatrix[j][0] = j;
			}

			for (let j = 1; j <= b.length; j += 1) {
				for (let i = 1; i <= a.length; i += 1) {
					const indicator = a[i - 1] === b[j - 1] ? 0 : 1;
					distanceMatrix[j][i] = Math.min(
						distanceMatrix[j][i - 1] + 1, // deletion
						distanceMatrix[j - 1][i] + 1, // insertion
						distanceMatrix[j - 1][i - 1] + indicator // substitution
					);
				}
			}
			return distanceMatrix[b.length][a.length];
		};

		this.onmessage = function (message) {
			var words = message.data.words;
			let target = message.data.target;
			let minimumDistance = 9999999;
			let word = '';

			for (let i = 0; i < words.length; i++) {
				let distance = levenshteinDistance(target, words[i]);
				if (distance === 0) {
					word = words[i];
					break;
				} else if (distance < minimumDistance) {
					minimumDistance = distance;
					word = words[i];
				} else {
					word = 'Error';
				}
			}

			this.postMessage(word);
		};
	};

	return {
		init: init
	};
})();

export default suggestion;
