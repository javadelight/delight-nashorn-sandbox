const sanitizerJs = require('./index');

function testSanitizer(inputCode, expectedOutputCode) {
  const insertCode = "__if();";
  const result = sanitizerJs(inputCode, insertCode);
  console.log(result);

  const normalize = (str) => str.replace(/\r\n/g, '\n').replace(/\n\s*\n/g, '\n\n').trim();

  expect(normalize(result)).toBe(normalize(expectedOutputCode));
}

describe('sanitizerJs', () => {
  it('should insert code at the beginning of for loops', () => {
    const inputCode = `
for (var i = 0; i < 10; i++) {
    console.log(i);
}
for (var i = 0; i < 10; i++) 
    console.log(i);
    `;
    const expectedOutputCode = `
for (var i = 0; i < 10; i++) {
    __if();
    console.log(i);
}
for (var i = 0; i < 10; i++) {
    __if();
    console.log(i);
}
    `;
    testSanitizer(inputCode, expectedOutputCode);
  });

  it('should insert code at the beginning of while loops', () => {
    const inputCode = `
while (true) {
    console.log('loop');
}
while (true) 
    console.log('loop');
    `;
    const expectedOutputCode = `
while (true) {
    __if();
    console.log('loop');
}
while (true) {
    __if();
    console.log('loop');
}
    `;
    testSanitizer(inputCode, expectedOutputCode);
  });

  it('should insert code at the beginning of do...while loops', () => {
    const inputCode = `
do {
    console.log('loop');
} while (true);
    `;
    const expectedOutputCode = `
do {
    __if();
    console.log('loop');
} while (true);
    `;
    testSanitizer(inputCode, expectedOutputCode);
  });

  it('should insert code at the beginning of function bodies', () => {
    const inputCode = `
function example() {
    console.log('inside function');
}
    `;
    const expectedOutputCode = `
function example() {
    __if();
    console.log('inside function');
}
    `;
    testSanitizer(inputCode, expectedOutputCode);
  });

  it('should insert code after every 10 statements', () => {
    const inputCode = `
var a = 1;
var b = 2;
var c = 3;
var d = 4;
var e = 5;
var f = 6;
var g = 7;
var h = 8;
var i = 9;
var j = 10;
var k = 11;
    `;
    const expectedOutputCode = `
var a = 1;
var b = 2;
var c = 3;
var d = 4;
var e = 5;
var f = 6;
var g = 7;
var h = 8;
var i = 9;
var j = 10;
__if();
var k = 11;
    `;
    testSanitizer(inputCode, expectedOutputCode);
  });

  it('should insert code after every 10 statements with if else', () => {
    const inputCode = `
function FindProxyForURL(url, host) {
    var i = 1;
    var i = 2;
    var i = 3;
    var i = 4;
    var i = 5;
    var i = 6;
    var i = 7;
    if (dnsDomainIs(host, 'proxy8.com.net')) {
        var i = 9;
        var i = 10;
        return 'PROXY http://proxy8.acme.com:8080';
    } else if (dnsDomainIs(host, 'acme1.com.net')) {
        var i = 9;
        var i = 10;
        return 'PROXY http://proxy9.acme.com:8080';
    } else if (dnsDomainIs(host, 'initech.acme.com')) {
        var i = 9;
        var i = 10;
        return 'PROXY http://acme10.com:8080';
    } else if (dnsDomainIs(host, 'whymper.net')) {
        return 'PROXY http://acme2.com:8080';
    } else if (dnsDomainIs(host, 'enough.acme.com')) {
        var i = 9;
        return 'PROXY http://one.proxy.acme.com:8080';
    } else {
        var i = 9;
        var i = 10;
        return 'DIRECT';
    }
    var i = 9;
    if (true)
        var i = 10;
}
    `;
    const expectedOutputCode = `
function FindProxyForURL(url, host) {
    __if();
    var i = 1;
    var i = 2;
    var i = 3;
    var i = 4;
    var i = 5;
    var i = 6;
    var i = 7;
    if (dnsDomainIs(host, 'proxy8.com.net')) {
        var i = 9;
        var i = 10;
        __if();
        return 'PROXY http://proxy8.acme.com:8080';
    } else if (dnsDomainIs(host, 'acme1.com.net')) {
        var i = 9;
        var i = 10;
        __if();
        return 'PROXY http://proxy9.acme.com:8080';
    } else if (dnsDomainIs(host, 'initech.acme.com')) {
        var i = 9;
        var i = 10;
        __if();
        return 'PROXY http://acme10.com:8080';
    } else if (dnsDomainIs(host, 'whymper.net')) {
        return 'PROXY http://acme2.com:8080';
    } else if (dnsDomainIs(host, 'enough.acme.com')) {
        var i = 9;
        return 'PROXY http://one.proxy.acme.com:8080';
    } else {
        var i = 9;
        var i = 10;
        __if();
        return 'DIRECT';
    }
    var i = 9;
    if (true) {
        __if();
        var i = 10;
    }
}
    `;
    testSanitizer(inputCode, expectedOutputCode);
  });

  it('should insert code after every 10 statements with switch', () => {
    const inputCode = `
function FindProxyForURL(url, host) {
    var i = 1;
    var i = 2;
    var i = 3;
    var i = 4;
    var i = 5;
    var i = 6;
    var i = 7;
    switch (a) {
      case 1:
          var i = 9;
          var i = 10;
          break;
      case 2:
          var i = 9;
          break;
      default:
          var i = 9;
          var i = 10;
          var i = 11;
    }
}
    `;
    var expectedOutputCode = `
function FindProxyForURL(url, host) {
    __if();
    var i = 1;
    var i = 2;
    var i = 3;
    var i = 4;
    var i = 5;
    var i = 6;
    var i = 7;
    switch (a) {
    case 1:
        var i = 9;
        var i = 10;
        __if();
        break;
    case 2:
        var i = 9;
        break;
    default:
        var i = 9;
        var i = 10;
        __if();
        var i = 11;
    }
}
    `
    testSanitizer(inputCode, expectedOutputCode);
  });

  it('should insert code for complex code', () => {
    const inputCode = `
function exampleFunction() {
    var a = 1;
    var a = 2;
    for (var i = 0; i < 5; i++) {
        var b = 1;
        var b = 2;
        var b = 3;
        b = 4;
        b = 5;
        b = 6;
        b = 7;
        if (b > a) {
            b = 9;
            b = 10;
            b = 11;
        } else {
            var b = 9;
            break;
        }
    }
    while (b < 10) {
        var c = 1;
        var c = 2;
        var c = 3;
        var c = 4;
        var c = 5;
        c = 6;
        c = 7;
        c = 8;
        c = 9;
        c = 10;
        b--;
    }
    do {
        var d = 1;
        var d = 2;
        var d = 3;
        var d = 4;
        var d = 5;
        d = 6;
        d = 7;
        d = 8;
        d = 9;
        d = 10;
        a++;
    } while (a < 10);

    switch (a) {
        case 1:
            var e = 1;
            var e = 2;
            var e = 3;
            var e = 4;
            var e = 5;
            e = 6;
            e = 7;
            e = 8;
            e = 9;
            e = 10;
            break;
        case 2:
            var i = 9;
            break;
        default:
            var j = 10;
    }

    function nestedFunction() {
        var k = 11;
        for (var j = 0; j < 3; j++) {
            var l = 12;
            while (l > 0) {
                var m = 13;
                l--;
            }
        }
    }
    nestedFunction();
}

exampleFunction();
    `;
    var expectedOutputCode = `
function exampleFunction() {
    __if();
    var a = 1;
    var a = 2;
    for (var i = 0; i < 5; i++) {
        __if();
        var b = 1;
        var b = 2;
        var b = 3;
        b = 4;
        b = 5;
        b = 6;
        b = 7;
        if (b > a) {
            b = 9;
            __if();
            b = 10;
            b = 11;
        } else {
            var b = 9;
            __if();
            break;
        }
    }
    while (b < 10) {
        __if();
        var c = 1;
        var c = 2;
        var c = 3;
        var c = 4;
        var c = 5;
        c = 6;
        c = 7;
        c = 8;
        c = 9;
        __if();
        c = 10;
        b--;
    }
    do {
        __if();
        var d = 1;
        var d = 2;
        var d = 3;
        var d = 4;
        var d = 5;
        d = 6;
        d = 7;
        d = 8;
        d = 9;
        __if();
        d = 10;
        a++;
    } while (a < 10);
    switch (a) {
    case 1:
        var e = 1;
        var e = 2;
        var e = 3;
        var e = 4;
        var e = 5;
        e = 6;
        e = 7;
        e = 8;
        __if();
        e = 9;
        e = 10;
        break;
    case 2:
        var i = 9;
        break;
    default:
        var j = 10;
    }
    function nestedFunction() {
        __if();
        var k = 11;
        for (var j = 0; j < 3; j++) {
            __if();
            var l = 12;
            while (l > 0) {
                __if();
                var m = 13;
                l--;
            }
        }
    }
    nestedFunction();
}
exampleFunction();
    `
    testSanitizer(inputCode, expectedOutputCode);
  });
});
