/**
 * Login manager
 */

const handleLogin = () => {
	document.getElementById("login-button").addEventListener("click", (elem) => {
		let form = elem.target.closest("form");
		const userDetails = {
			username: document.getElementById("username-input").value,
			password: document.getElementById("password-input").value,
		}
		if (form.checkValidity()) {
			makeCall("POST", "LoginChecker", userDetails, (res) => {
				const message = res.message;
				switch (res.status) {
					case 200:
						sessionStorage.setItem('username', message);
						window.location.href = "Home.html";
						break;
					default:
						document.getElementById("error-message").innerText = message;
						setTimeout(function () {
							document.getElementById("error-message").innerText = "";
						}, 3000);
				};
			});
		} else {
			form.reportValidity();
		};
	});
}

const handleSignup = () => {
	document.getElementById("register-button").addEventListener("click", (elem) => {
		let form = elem.target.closest("form");
		const userDetails = {
			username: document.getElementById("username-input").value,
			password: document.getElementById("password-input").value,
		}
		if (form.checkValidity()) {
			makeCall("POST", "Register", userDetails, (res) => {
				const message = res.message;
				switch (res.status) {
					case 200:
						sessionStorage.setItem('username', message);
						window.location.href = "Home.html";
						break;
					default:
						document.getElementById("error-message").innerText = message;
						setTimeout(function () {
							document.getElementById("error-message").innerText = "";
						}, 3000);
				};
			});
		} else {
			form.reportValidity();
		};
	});
}

const setPageLanguage = () => {
	let title = document.getElementById("title");
	let login = document.getElementById("login-button");
	let register = document.getElementById("register-button");
	let username = document.getElementById("username-input");
	let password = document.getElementById("password-input");
	let produced = document.getElementById("produced-by");
	let person = document.getElementsByClassName("person-code");
	
	title.innerText = LANG.Title;
	login.setAttribute("value", LANG.Login);
	register.setAttribute("value", LANG.Register);
	username.setAttribute("placeholder", LANG.UsernameHint);
	password.setAttribute("placeholder", LANG.PasswordHint);
	produced.innerText = LANG.ProducedBy;
	for (let i in person) {
		if (person[i].innerText !== undefined) {
			person[i].innerText = LANG.PersonCode + ":" + person[i].innerText.split(":")[1];
		}
	}
}

const main = () => {
	setPageLanguage();
	handleLogin();
	handleSignup();
};

main();
