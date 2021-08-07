/**
 * AJAX call management
 */

const makeCall = async (method, url, formElement, callback) => {
	var params = {
			method: method,
			headers: {
				"Content-type": "application/json",
			},
			...(method == "POST" && {body: JSON.stringify(formElement)}),
		};
	const response = await fetch(url, params);
	// Pass the response to the callback
	const responseData = await response.json();
	callback(responseData);
};


