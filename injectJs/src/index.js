const acorn = require("acorn");
const estraverse = require("estraverse");
const escodegen = require("escodegen");

const ecmaVersion = 2020;

/**
 * analyze the code and insert the insertCode
 * @param code
 * @param insertCode
 * @returns {{code: string, map: null}|*|{code: *}}
 */
const injectJs = function (code, insertCode) {
  const ast = acorn.parse(code, { sourceType: "module", ecmaVersion: ecmaVersion });
  let statementCount = 0;
  let statementStack = [];

  const insertCodeBody = acorn.parse(insertCode, { ecmaVersion: ecmaVersion }).body[0];

  estraverse.replace(ast, {
    enter: function (node, parent) {
      // save statementCount into stack
      if (isIfBlockStatement(node, parent) || isSwitchCaseStatement(node,parent)) {
        statementStack.push(statementCount);
      }

      // insert code after every 10 statements
      if (node.type === "ExpressionStatement" || node.type === "VariableDeclaration") {
        statementCount++;

        if (statementCount >= 10) {
          if (parent.body) {
            parent.body.splice(parent.body.indexOf(node) + 1, 0, insertCodeBody);
            statementCount = 0;
          } else if (parent.consequent) {
            if (parent.type === "SwitchCase") {
              parent.consequent.splice(parent.consequent.indexOf(node) + 1, 0, insertCodeBody);
            } else {
              // IfStatement with no braces
              const newBody = {
                type: "BlockStatement",
                body: [insertCodeBody, node]
              };
              parent.consequent = newBody;
            }
            statementCount = 0;
          }
        }
      }

      // insert code at the beginning of function body, for, while and do-while loops
      if (node.type === "FunctionDeclaration" || node.type === "FunctionExpression"
          || node.type === "ForStatement" || node.type === "WhileStatement" || node.type === "DoWhileStatement") {
        if (node.body.type === "BlockStatement") {
          node.body.body.unshift(insertCodeBody);
        } else {
          // no braces
          const newBody = {
            type: "BlockStatement",
            body: [insertCodeBody, node.body]
          };
          node.body = newBody;
        }
        statementCount = 0;
        return node;
      }
    },
    leave: function (node, parent) {
      // restore statementCount from stack
      if (isIfBlockStatement(node, parent) || isSwitchCaseStatement(node,parent)) {
        statementCount = statementStack.pop();
      }
    }
  });

  function isIfBlockStatement(node, parent) {
    return node.type === "BlockStatement" && parent.type === "IfStatement";
  }

  function isSwitchCaseStatement(node, parent) {
    return node.type === "SwitchCase" && parent.type === "SwitchStatement";
  }

  return escodegen.generate(ast);
};

module.exports = injectJs;
