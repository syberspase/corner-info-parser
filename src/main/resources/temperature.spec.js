// variable to be binded by Java
var value;

// variable to return Java eval
var isValid = isInt();

// Checks if value is int.
function isInt() {
	return  (Number(value) === parseInt(value, 10));
}
