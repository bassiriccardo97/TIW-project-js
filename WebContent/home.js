/**
 * Homepage after login
 */

let CATEGORIES = []
let UPDATES = []
let FILES = []
let ROW_INDEX = -1;
let COLUMN_INDEX = -1;
let SLIDE_INDEX = 1;
let IMAGES = [];
let ID_FOR_LANGUAGE = [];

// Utils function

const setElemAttributes = (el, attrs) => {
	for(var key in attrs) {
    	el.setAttribute(key, attrs[key]);
  	}
}

const displayInfoMessage = (elem, error, message) => {
	elem.innerHTML = message;
	if (error === true) {
		elem.style.color = "red";
	} else {
		elem.style.color = "green";
	}
	setTimeout(function () {
		document.getElementById("info-message").innerHTML = "";
		document.getElementById("info-message-images").innerHTML = "";
		document.getElementById("info-message-rename").innerHTML = "";
	}, 3000)
};

const calculateNextIndex = (fatherId) => {
	return `${CATEGORIES.filter(category => category.fatherId == fatherId).length}`;
}

const reCalculateIndexes = (fatherId, indexRemoved) => {
	CATEGORIES.filter(category => category.fatherId == fatherId).map(category => {
		const index = parseInt(category.index);
		indexRemoved = parseInt(indexRemoved);
		if (index > indexRemoved) {
			category.index = `${category.index - 1}`;
		}
	})
}

const getAllFathers = (id) => {
	const fathers = [id]
	let tempId = id
	while (tempId > 0) {
		CATEGORIES.map(cat => {
			if (cat.id == tempId) {
				fathers.push(cat.fatherId)
				tempId = cat.fatherId
			}
		})
	}
	return fathers
}

// Handle updates queue
const addNewUpdate = (categoryId, newFatherId = null, categoryNewIndex) => {
	UPDATES.push({sourceId: categoryId, destinationId: newFatherId, categoryNewIndex: categoryNewIndex});
}

const handlePostChanges = () => {
	const root = document.getElementById("submit-changes");
	if (root.innerHTML === "") {
		const save = document.createElement("input");
		const cancel = document.createElement("input");
		setElemAttributes(save, {"id": "save-button", "class": "button button-filled", "type": "button", "value": LANG.Save});
		setElemAttributes(cancel, {"id": "cancel-button", "class": "button button-filled", "type": "button", "value": LANG.Cancel});
		save.addEventListener("click", (event) => {
			makeCall("POST", "MoveCategories", UPDATES, (res) => {
				switch (res.status) {
					case 200:
						//const message = JSON.parse(res.message);
						displayInfoMessage(document.getElementById("info-message"), false, LANG.ChangesConfirmed);
						UPDATES = []
						break;
					default:
						displayInfoMessage(document.getElementById("info-message"), true, LANG.GenericError);
				};
			})
			root.style.display = "none";
		});
		cancel.addEventListener("click", () => {
			window.location.reload();
		});
		root.appendChild(cancel);
		root.appendChild(save);
	} else {
		root.style.display = "flex";
	}
}

// Drag and drop functions

const dragStartHandler = (event) => {
	event.dataTransfer.setData("text/plain", event.target.id);
}

const dropOverHandler = (event) => {
	if (event.target.tagName != "LI") {
		return;
	}
	event.preventDefault();
	const categoryId = parseInt(event.dataTransfer.getData("text/plain"));
	const destinationCategoryId = parseInt(event.target.id);
	const destinationIdFathers = getAllFathers(destinationCategoryId);
	
	if (!destinationIdFathers.includes(categoryId)) {
		document.getElementById("info-message").innerText = "";
		if (confirm(LANG.Confirm)) {
			moveCategory(categoryId, destinationCategoryId);
			addNewUpdate(categoryId, destinationCategoryId);
			handlePostChanges();
			displayCategoriesTree();
			makeFatherIdSelector();
		}
	} else {
		displayInfoMessage(document.getElementById("info-message"), true, LANG.GenericError);
	}
}

const dragEnter = (event) => {
	// TODO: handle this better
	if (event.target.classList.contains("category")) {
		document.getElementById(event.target.id).classList.add("category-drag-over");	
	}
}

const dragLeave = (event) => {
	//console.log("Leaving: ", event.target)
	if (event.target.classList.contains("category")) {
		document.getElementById(event.target.id).classList.remove("category-drag-over");
	}
}

const allowDrop = (event) => {
	event.preventDefault();
}

// Functions to handle CREATE / UPDATE / DELETE client side
const addCategory = (catId, name, fatherId) => {
	const category = {
		id: catId,
		name: name,
		fatherId: fatherId,
		index: calculateNextIndex(fatherId),
	}
	CATEGORIES.push(category);
}

const moveCategory = (categoryId, newFatherId) => {
	const categoryToBeMoved = CATEGORIES.filter(category => category.id == categoryId)[0]
	const oldFatherId = categoryToBeMoved.fatherId;
	const oldIndex = categoryToBeMoved.index;
	const newIndex = calculateNextIndex(newFatherId);
	categoryToBeMoved.fatherId = newFatherId;
	categoryToBeMoved.index = newIndex;
	reCalculateIndexes(oldFatherId, oldIndex);
}

const deleteCategory = (categoryId, fatherId) => {
	const category = {
		id: categoryId,
		fatherId: fatherId,
	}
	makeCall("POST", "DeleteCategory", category, (res) => {
		switch (res.status) {
			case 200:
				let index;
				for (let c in CATEGORIES) {
					if (CATEGORIES[c].id === category.id) {
						index = CATEGORIES[c].index;
						CATEGORIES.splice(c, 1);
						break;
					}
				}
				reCalculateIndexes(category.fatherId, index);
				let message = JSON.parse(res.message);
				for (let i in message) {
					for (let j = 0; j < CATEGORIES.length; j++) {
						if (CATEGORIES[j].id === message[i].id) {
							CATEGORIES.splice(j, 1);
						}
					}
				}
				displayCategoriesTree();
				makeFatherIdSelector();
				break;
			default:
				displayInfoMessage(document.getElementById("info-message"), true, LANG.GenericError);
				break;
		};
	})
}

const addDeleteEventListener = (element, categoryId, fatherId) => {
	element.addEventListener("click", (e) => {
		deleteCategory(categoryId, fatherId)
		e.stopPropagation();
	})
}

const addRenameEventListener = (element, categoryId) => {
	element.addEventListener("click", (e) => {
		let categoryName = "";
		for (i in CATEGORIES) {
			if (CATEGORIES[i].id === categoryId) {
				categoryName = CATEGORIES[i].name;
				break;
			}
		}
		renameCategoryModal(categoryId, categoryName);
		e.stopPropagation();
	})
}

const renameCategoryModal = (categoryId, categoryName) => {
	var modal = document.getElementById("rename-modal");
	
	var span = document.getElementsByClassName("close1")[1];
	
	document.getElementById("rename-modal-title").innerText = LANG.RenameTitle + categoryName;
	
	const renameButton = document.getElementById("rename-category-button");
	let oldElement = renameButton;
	let newElement = oldElement.cloneNode(true);
	oldElement.parentNode.replaceChild(newElement, oldElement);
	newElement.addEventListener("click", (elem) => {
		let form = elem.target.closest("form");
		let name = document.getElementById("category-to-rename-name").value
		const renameDetails = {
			"name": name,
			"id": categoryId
		}
		if (form.checkValidity()) {
			makeCall("POST", "RenameCategory", renameDetails, (res) => {
				switch (res.status) {
					case 200:
						modal.style.display = "none";
						let p = document.getElementById(categoryId).children[0];
						let inner = p.innerHTML;
						inner = inner.replace(categoryName, name);
						p.innerHTML = inner;
						for (i in CATEGORIES) {
							if (CATEGORIES[i].id === categoryId) {
								CATEGORIES[i].name = name;
								break;
							}
						}
						makeFatherIdSelector();
						break;
					default:
						displayInfoMessage(document.getElementById("info-message-rename"), true, LANG.GenericError);
				};
				form.reset();
			});
		} else {
			form.reportValidity();
		};
	});
	
	modal.style.display = "block";
	
	span.onclick = function() {
	  modal.style.display = "none";
	}
	
	window.onclick = function(event) {
	  if (event.target == modal) {
	    modal.style.display = "none";
	  }
	} 
}

const downloadImage = (event) => {
	let image = event.target.id.split("download-image-button-")[1];
	window.location.href = "DownloadImage?filename=" + image;
}

const deleteImage = (event) => {
	let image = event.target.id.split("delete-image-button-")[1];
	makeCall("GET", "DeleteImage?filename=" + image, null, (res) => {
		switch (res.status) {
			case 200:
				let elementToRemove = document.getElementById("container-" + image);
				let lastRow = document.getElementById("row-" + ROW_INDEX);
				let lastImage = lastRow.lastChild;
				
				let deleted = -1;
				for (let i = 0; i < IMAGES.length; i++) {
					if (IMAGES[i] === image) {
						deleted = i;
						if (i < IMAGES.length - 1) {
							IMAGES.splice(i, 1, IMAGES.pop());
							document.getElementById("container-" + IMAGES[i]).children[0].setAttribute("onclick", "openGalleryModal();currentSlide(" + (i + 1) + ")");
						} else {
							IMAGES.pop();
						}
					}
					if (i > deleted && deleted !== -1) {
						document.getElementById("container-" + IMAGES[i]).children[0].setAttribute("onclick", "openGalleryModal();currentSlide(" + (i + 1) + ")");
					}
				}
				COLUMN_INDEX--;
				if (lastImage !== elementToRemove) {
					let tempId = lastImage.id;
					let tempHTML = lastImage.innerHTML;
					elementToRemove.innerHTML = tempHTML;
					elementToRemove.id = tempId;
				}
				lastImage.remove(); 
				if (lastRow.children.length === 0) {
					lastRow.remove();
					ROW_INDEX--;
					COLUMN_INDEX = -1;
				}
				
				if (document.getElementById("images-list").children.length === 1) {
					document.getElementById("no-images").innerText = LANG.EmptyList;
				}
				break;
			default:
				displayInfoMessage(document.getElementById("info-message-images"), true, LANG.GenericError);
		};
	});
}

const onChange = (event) => {
	function readmultifiles(files) {
  		var reader = new FileReader();  
	  	function readFile(index) {
	    	if( index >= files.length ) {
				return;
			};
    		var file = files[index];
			if (file.type !== "image/jpeg") {
				displayInfoMessage(document.getElementById("info-message-images"), true, LANG.GenericError);
				FILES = [];
				return;
			}
			let f = {
				name: file.name,
				file: ''
			};
	    	reader.onload = function(e) {
		      	var bin = e.target.result;
				f.file = bin;
				FILES[FILES.length] = f;
		      	readFile(index+1)
	    	}
    		reader.readAsDataURL(file);
  		}
 	 	readFile(0);
	}
	let files = event.target.files;
	readmultifiles(files);
}

const calculateIndexToPrint = () => {
	CATEGORIES.map(category => {
		let tempCategory = category;
		let tempIndex = `${parseInt(category.index) + 1}`;
		while (tempCategory.fatherId != 0) {
			tempCategory = CATEGORIES.filter(cat => (cat.id == tempCategory.fatherId))[0];
			tempIndex = `${parseInt(tempCategory.index) + 1}.${tempIndex}`;
		}
		category.indexToPrint = tempIndex;
	});
};

const displayCategoriesTree = () => {
	let root = document.getElementById("categories-list");
	root.innerHTML = ""; // Cleaning the tree to repopulate it, in order to avoid reloading the page
	calculateIndexToPrint();
	CATEGORIES.sort((cat1, cat2) => {
		return cat1.indexToPrint.localeCompare(cat2.indexToPrint);
	});
	//console.log(CATEGORIES);
	CATEGORIES.map(category => {
		const spacing = "&nbsp;&nbsp;&nbsp;&nbsp;".repeat(category.indexToPrint.split(".").length - 1);
		let newCategoryElement = document.createElement("li");
		let categoryName = document.createElement("p");
		let controlButtons = document.createElement("div");
		let renameButton = document.createElement("input");
		let deleteButton = document.createElement("input");
		
		setElemAttributes(newCategoryElement, {"draggable": "true", "ondragstart": "dragStartHandler(event)", "ondrop": "dropOverHandler(event)", "ondragover": "allowDrop(event)", "ondragenter": "dragEnter(event)", "ondragleave": "dragLeave(event)", "class": "category", "id": category.id});
		
		categoryName.setAttribute("class", "category-name");
		categoryName.innerHTML = "<b>" + spacing + category.indexToPrint + "</b>" + category.name;
		
		controlButtons.setAttribute("class", "category-control-buttons");
		
		setElemAttributes(renameButton, {"type": "button", "id": "rename-button-" + category.id, "class": "button half-button button-filled category-button", "value": LANG.Rename});
		setElemAttributes(deleteButton, {"type": "button", "id": "delete-button-" + category.id, "class": "button half-button button-filled category-button", "value": LANG.Delete});
		
		controlButtons.append(renameButton, deleteButton);
		newCategoryElement.append(categoryName, controlButtons);
		
		root.append(newCategoryElement);
		//const categoryElement = document.getElementById(category.id);
		const deleteButtonCategoryElement = document.getElementById(`delete-button-${category.id}`);
		const renameButtonCategoryElement = document.getElementById(`rename-button-${category.id}`);
		addDeleteEventListener(deleteButtonCategoryElement, category.id, category.fatherId);
		addRenameEventListener(renameButtonCategoryElement, category.id);
		addImagesListEventListener(categoryName, category.id);
	});
};

const addImagesListEventListener = (element, categoryId, categoryName) => {
	element.addEventListener("click", () => {
		imagesListModal(categoryId, categoryName);
	})	
};

const imagesListModal = (categoryId) => {
	makeCall("GET", "ImagesList?category-id=" + categoryId, null, (res) => {
		const message = JSON.parse(res.message);
		switch (res.status) {
			case 200:
				var modal = document.getElementById("images-list-modal");
				
				var span = document.getElementsByClassName("close1")[0];
				
				let categoryName = "";
				for (i in CATEGORIES) {
					if (CATEGORIES[i].id === categoryId) {
						categoryName = CATEGORIES[i].name;
						break;
					}
				}
				
				document.getElementById("images-list-modal-title").innerText = LANG.ListTitle + categoryName;
				
				var list = document.getElementById("images-list");
				list.innerHTML = "";
				
				let newCatContainer = document.createElement("div");
				setElemAttributes(newCatContainer, {"id": "upload-form-container", "class": "new-category-container"});
				
				let form = document.createElement("form");
				setElemAttributes(form, {"id": "new-category-form-" + categoryId, "class": "new-category-form", "action": "#", "enctype": "multipart/form-data"});
				
				let label = document.createElement("label");
				label.innerText = LANG.AddImageHint;
				
				let fatherId = document.createElement("input");
				setElemAttributes(fatherId, {"type": "hidden", "id": "father-id", "name": "father-id", "value": categoryId});
				
				let file = document.createElement("input");
				setElemAttributes(file, {"type": "file", "id": "image-uploads", "name": "image-uploads", "accept": "image/*", "onchange": "onChange(event)", "multiple": ""});
				
				let button = document.createElement("input");
				setElemAttributes(button, {"type": "button", "class": "button button-filled", "value": LANG.Upload, "onclick": "uploadImage(event)"});
				
				form.append(label, fatherId, file, button);
				newCatContainer.append(form);
				list.append(newCatContainer);
				
				if (message.length === 0) {
					document.getElementById("no-images").innerText = LANG.EmptyList;
				} else {
					document.getElementById("no-images").innerText = '';
					var row;
					for (var i = 0; i < message.length; i++) {
						COLUMN_INDEX++;
						if (COLUMN_INDEX === 0) {
							ROW_INDEX++;
							row = document.createElement("div");
								setElemAttributes(row, {"id": "row-" + ROW_INDEX, "class": "row"});
							list.appendChild(row);
						}
						let container = document.createElement("div");
						setElemAttributes(container, {"class": "image-container", "id": "container-" + message[i]});
						
						let img = document.createElement("img");
						setElemAttributes(img, {"src": "ShowImage?filename=" + message[i], "class": "list hover-shadow center", "onclick": "openGalleryModal();currentSlide(" + (ROW_INDEX * 4 + 1 + COLUMN_INDEX) + ")"});
						let download = document.createElement("input");
						download.innerText = "Download";
						setElemAttributes(download, {"class": "button half-button button-filled category-button", "id": "download-image-button-" + message[i], "type": "button", "onclick": "downloadImage(event)", "value": LANG.Download});
						let del = document.createElement("input");
						del.innerText = "Delete";
						setElemAttributes(del, {"class": "button half-button button-filled category-button", "id": "delete-image-button-" + message[i], "type": "button", "onclick": "deleteImage(event)", "value": LANG.Delete});
						let middle = document.createElement("div");
						middle.setAttribute("class", "middle category-control-buttons");
						middle.append(download, del);
						container.append(img, middle);
						row.appendChild(container);
						
						IMAGES[IMAGES.length] = message[i];
						
						if (COLUMN_INDEX === 3) {
							COLUMN_INDEX = -1;
						}
					}
				}
				modal.style.display = "block";
				
				span.onclick = function() {
				  	modal.style.display = "none";
					ROW_INDEX = -1;
					COLUMN_INDEX = -1;
					IMAGES = [];
				}
				
				window.onclick = function(event) {
				  	if (event.target == modal) {
				    	modal.style.display = "none";
						ROW_INDEX = -1;
						COLUMN_INDEX = -1;
						IMAGES = [];
				  	}
				} 
				break;
			default:
				displayInfoMessage(document.getElementById("info-message-images"), true, LANG.GenericError);
				break;
		};
	})
}

const uploadImage = (event) => {
	let form = event.target.closest("form");
	if (form.checkValidity()) {
		if( FILES.length === 0 ){
			displayInfoMessage(document.getElementById("info-message-images"), true, LANG.GenericError);
		} else {
			let id = document.getElementById("father-id").value;
			var data = {
				fatherId: id,
				files: [],
				fileNames: []
			};
			for (let f in FILES) {
				data.files[data.files.length] = FILES[f].file;
				data.fileNames[data.fileNames.length] = FILES[f].name;
			}
			makeCall("POST", "AddImage", data, (res) => {
				switch (res.status) {
					case 200:
						const message = JSON.parse(res.message);
						document.getElementById("no-images").innerText = "";
						var list = document.getElementById("images-list");
						var row;
						for (var i = 0; i < message.length; i++) {
							COLUMN_INDEX++;
							if (COLUMN_INDEX === 0) {
								ROW_INDEX++;
								row = document.createElement("div");
								setElemAttributes(row, {"id": "row-" + ROW_INDEX, "class": "row"});
								list.appendChild(row);
							} else {
								row = document.getElementById("row-" + ROW_INDEX);
							}
							let container = document.createElement("div");
							setElemAttributes(container, {"class": "image-container", "id": "container-" + message[i]});
							
							let img = document.createElement("img");
							setElemAttributes(img, {"src": "ShowImage?filename=" + message[i], "class": "list hover-shadow", "onclick": "openGalleryModal();currentSlide(" + (ROW_INDEX * 4 + 1 + COLUMN_INDEX) + ")"});
							let download = document.createElement("input");
							download.innerText = "Download";
							setElemAttributes(download, {"class": "button half-button button-filled category-button", "id": "download-image-button-" + message[i], "type": "button", "onclick": "downloadImage(event)", "value": LANG.Download});
							let del = document.createElement("input");
							del.innerText = "Delete";
							setElemAttributes(del, {"class": "button half-button button-filled category-button", "id": "delete-image-button-" + message[i], "type": "button", "onclick": "deleteImage(event)", "value": LANG.Delete});
							let middle = document.createElement("div");
							middle.setAttribute("class", "middle category-control-buttons");
							middle.append(download, del);
							container.append(img, middle);
							row.appendChild(container);
							
							IMAGES[IMAGES.length] = message[i];
							
							if (COLUMN_INDEX === 3) {
								COLUMN_INDEX = -1;
							}
						}
						break;
					default:
						displayInfoMessage(document.getElementById("info-message-images"), true, LANG.GenericError);
						break;
				};
				form.reset();
			});
			FILES = [];
		}
	} else {
		form.reportValidity();
	}
}

const openGalleryModal = () => {
	document.getElementById("gallery-modal").style.display = "block";
	
	var slidesContainer =  document.getElementById("slides-container");
	var cursorContainer =  document.getElementById("cursor-container");
	
	for (let i = 0; i < IMAGES.length; i++) {
		var temp = IMAGES[i].split("--")[1];
		var name = temp.split(".")[0];
		let slide = document.createElement("div");
		slide.setAttribute("class", "mySlides");
		let numberText = document.createElement("div");
		numberText.setAttribute("class", "numbertext");
		numberText.innerText = i + 1 + " / " + IMAGES.length;
		let slideImg = document.createElement("img");
		setElemAttributes(slideImg, {"src": "ShowImage?filename=" + IMAGES[i], "class": "slide-image"});
		let column = document.createElement("div");
		column.setAttribute("class", "column");
		let columnImg = document.createElement("img");
		setElemAttributes(columnImg, {"class": "demo cursor cursor-image", "src": "ShowImage?filename=" + IMAGES[i], "onclick": "currentSlide(" + (i + 1) + ")", "alt": name});
		
		slide.append(numberText, slideImg);
		slidesContainer.append(slide);
		column.append(columnImg);
		cursorContainer.append(column);
	}
	
	showSlides(SLIDE_INDEX);
}

const closeGalleryModal = () => {
  	document.getElementById("gallery-modal").style.display = "none";
	let slidesContainer = document.getElementById("slides-container");
	let cursorContainer = document.getElementById("cursor-container");
	
	slidesContainer.innerHTML = "";
	cursorContainer.innerHTML = "";
}

const plusSlides = (n) => {
	showSlides(SLIDE_INDEX += n);
}

const currentSlide = (n) => {
	showSlides(SLIDE_INDEX = n);
}

const showSlides = (n) => {
	var i;
  	var slides = document.getElementsByClassName("mySlides");
  	var dots = document.getElementsByClassName("demo");
  	var captionText = document.getElementById("caption");
  	if (n > slides.length) {SLIDE_INDEX = 1}
  	if (n < 1) {SLIDE_INDEX = slides.length}
  	for (i = 0; i < slides.length; i++) {
		slides[i].style.display = "none";
  	}
  	for (i = 0; i < dots.length; i++) {
		dots[i].className = dots[i].className.replace(" active", "");
  	}
  	slides[SLIDE_INDEX-1].style.display = "block";
  	dots[SLIDE_INDEX-1].className += " active";
  	captionText.innerHTML = dots[SLIDE_INDEX-1].alt;
}

const makeFatherIdSelector = () => {
	const selector = document.getElementById("new-category-father-id");
	selector.innerHTML = ""; // Cleans the selector
	selector.innerHTML += `<option value="0">${LANG.RootCategory}</option>`; // Root category
	CATEGORIES.map(category => {
		const option = `<option value="${category.id}">${category.name}</option>`;
		selector.innerHTML += option;
	});
};

const getCategories = () => {
	makeCall("GET", "GetCategories", null, (res) => {
		const message = JSON.parse(res.message);
		switch (res.status) {
			case 200:
				CATEGORIES = [...message];
				displayCategoriesTree();
				makeFatherIdSelector();
				setPageLanguage();
				break;
			default:
				setPageLanguage();
				displayInfoMessage(document.getElementById("info-message"), true, LANG.GenericError);
		};
	});
}

const postNewCategory = (event) => {
	let form = event.target.closest("form");
	if (form.checkValidity()) {
		const newCategory = {
			name: document.getElementById("new-category-name").value,
			fatherId: parseInt(document.getElementById("new-category-father-id").value, 10),
		};
	
		makeCall("POST", "AddNewCategory", newCategory, (res) => {
			switch (res.status) {
				case 200:
					let catId = JSON.parse(res.message);
					addCategory(catId, newCategory.name, newCategory.fatherId);
					displayCategoriesTree();
					makeFatherIdSelector();
					break;
				default:
					displayInfoMessage(document.getElementById("info-message"), true, LANG.GenericError);
					break;
			};
			form.reset();
		});
	} else {
		form.reportValidity();
	}
};

const postNewCategoryEventListener = () => {
	document.getElementById("new-category-button").addEventListener("click", (e) => {
		postNewCategory(e);
	});
};

const setPageLanguage = () => {
	let buttons = document.getElementsByClassName("category-button");
	let welcome = document.getElementById("welcome");
	let title = document.getElementById("title");
	let logout = document.getElementById("logout-button");
	let newCategory = document.getElementById("new-category-name");
	let newCategoryButton = document.getElementById("new-category-button");
	let saveButton = document.getElementById("save-button");
	let enterName = document.getElementById("p-enter-name");
	let rename = document.getElementById("rename-category-button");
	let renameHint = document.getElementById("category-to-rename-name");
	let produced = document.getElementById("produced-by");
	let person = document.getElementsByClassName("person-code");
	
	title.innerText = LANG.Title;
	welcome.innerText = LANG.Welcome + sessionStorage.getItem("username");
	logout.innerText = LANG.Logout;
	newCategory.setAttribute("placeholder", LANG.NewCategoryHint);
	newCategoryButton.setAttribute("value", LANG.Create);
	enterName.innerText = LANG.NewName;
	rename.setAttribute("value", LANG.Rename);
	renameHint.setAttribute("placeholder", LANG.RenameCategoryHint);
	if (saveButton !== null) {
		saveButton.setAttribute("value", LANG.Save);
	}
	for (i in buttons) {
		if (buttons[i].id !== undefined) {
			if (buttons[i].id.includes("rename")) {
				buttons[i].setAttribute("value", LANG.Rename);
			} else if (buttons[i].id.includes("delete")) {
				buttons[i].setAttribute("value", LANG.Delete);
			}
		}
	}
	makeFatherIdSelector();
	produced.innerText = LANG.ProducedBy;
	for (let i in person) {
		if (person[i].innerText !== undefined) {
			person[i].innerText = LANG.PersonCode + ":" + person[i].innerText.split(":")[1];
		}
	}
}

const handleLogout = () => {
	makeCall("GET", "Logout", null, (res) => {
		switch (res.status) {
			case 200:
				window.location.href = "index.html";
				break;
			default:
				displayInfoMessage(document.getElementById("info-message"), true, LANG.GenericError);
				break;
		};
	});
}

window.onload = () => {
	if (sessionStorage.getItem("language") !== null) {
		setLanguage(sessionStorage.getItem("language"));
	}
	getCategories();
	postNewCategoryEventListener();
};
