var Suggestion = (function () {
	var init = function () {
		console.log('Initialize module');
		addHandler();
	};

	var addHandler = function () {
		var textarea = document.getElementById('box');
		var result = document.getElementById('result');

		textarea.onkeyup = function () {
			var words = this.value.split(' ');

			var target = (words[words.length - 1] !== '' || words[words.length - 1].length < 2 ? words[words.length - 1] : words[words.length - 2]);

			console.log(target);

			result.innerHTML = target;
		};
	};

	var workerFunction = function () {
	};

	/**
	 * @param {string} a
	 * @param {string} b
	 * @return {number}
	 */
	var levenshteinDistance = function (a, b) {
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

	return {
		init: init
	};
})();

export default Suggestion;
