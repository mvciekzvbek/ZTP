import $ from 'jquery';

let suggestion = (() => {
	const threads = navigator.hardwareConcurrency;
	let workers = [];
	const textarea = $('#box');
	const resultBox = $('#result');
	let start;
	let interval = null;
	let isIntervalSet = false;
	let found = false;
	let result = '';
	let words = {};

	/**
	 * initializes application, adds listeners and prepares threads
	 */
	let init = () => {
		prepareThreads();

		textarea.keyup(() => {
			if (!isIntervalSet) {
				isIntervalSet = true;
				found = false;
				interval = setInterval(() => {
					var text = textarea.val();
					var textArr = text.split(' ');

					if (textArr[textArr.length - 1] !== '') {
						start = Date.now();
						findResult(textArr[textArr.length - 1].toLowerCase());
					}
				}, 3000);
			}
		});

		resultBox.bind('DOMSubtreeModified', () => {
			clearInterval(interval);
			isIntervalSet = false;
		});
	};

	/**
	 * creates fake url for worker
	 * @param {*} f 
	 */
	let hackFunctionToBuffer = (f) => window.URL.createObjectURL(new Blob([`(${f.toString()})()`], { type: 'application/javascript' }));

	/**
	 * prepares threads, sends divided txt file 
	 */
	let prepareThreads = () => {
		$.ajax({
			url: 'slowa.txt',
			async: true,
			success: (data) => {
				const lines = data.split('\n');
				for (let i = 0, n = lines.length; i < n; ++i) {
					let line = lines[i].slice(0, -1);
					const index = i % threads;
					words[index] = words[index] || [];
					words[index].push(line);
				}

				var workerBlob = hackFunctionToBuffer(workerFunction);

				for (let i = 0; i < threads; i++) {
					var worker = new Worker(workerBlob);
					workers.push(worker);

					// send just an array
					worker.postMessage({
						content: 'array',
						words: words[i]
					});
				}
			}
		});
	};

	/**
	 * printing result
	 * 
	 * @param {string} word 
	 */
	let finish = (word) => {
		result = word;
		resultBox.html(result + ', ' + (Date.now() - start) / 1000 + 's');
	};

	/**
	 * communicates with workers to find the result
	 * 
	 * @param {string} target 
	 */
	let findResult = (target) => {
		for (let j = 0, n = workers.length; j < n; j++) {
			let worker = workers[j];

			// send
			worker.postMessage({
				content: 'target',
				word: target
			});

			// recv
			worker.onmessage = (e) => {
				const word = e.data.word;
				const distance = e.data.distance;

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

	/**
	 * worker function
	 */
	var workerFunction = function () {
		let words = [];

		/**
		 * calculates levenshtein distance between two words
		 * @param {string} a
		 * @param {string} b
		 * @return {number}
		 */
		let levenshteinDistance = (a, b) => {
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
						distanceMatrix[j][i - 1] + 1, // devarion
						distanceMatrix[j - 1][i] + 1, // insertion
						distanceMatrix[j - 1][i - 1] + indicator // substitution
					);
				}
			}
			return distanceMatrix[b.length][a.length];
		};

		this.onmessage = (e) => {
			let msg = e.data;
			switch (msg.content) {
				case 'array':
					words = msg.words;
					break;
				case 'target':
					const target = msg.word;
					let minimumDistance = 3;
					let word = '';
					if (words.indexOf(target) !== -1) {
						word = target;
						minimumDistance = 0;
					} else {
						for (let i = 0, n = words.length; i < n; i++) {
							const distance = levenshteinDistance(target, words[i]);
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
