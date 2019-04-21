// variable to be binded by Java
var value;

// variable to return Java eval
var isValid = checkValidation();

// Checks if length of value is >= 3.
function checkValidation() {
	return value.length > 2;
}
