// variable to be binded by Java
var value;

// variable to return Java eval
var isValid = isFloat();

// Checks if value is float.
function isFloat() {
	return  !isNaN(Number(value)) && !isNaN(parseFloat(value));
}
