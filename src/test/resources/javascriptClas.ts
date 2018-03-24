class Tested {
    constructor(name:string){
        this._name = name;
    }
    _name:string;

    getName() {
        return this._name;
    }
}

let testedObject = new Tested("tested object");