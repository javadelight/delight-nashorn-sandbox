var Tested = /** @class */ (function () {
    function Tested(name) {
        this._name = name;
    }
    Tested.prototype.getName = function () {
        return this._name;
    };
    return Tested;
}());
var testedObject = new Tested("tested object");
