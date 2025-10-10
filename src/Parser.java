import java.util.Vector;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Parser {
    private final Vector<Tokenizer.Token> tokens;
    private int position;

    public Parser(Vector<Tokenizer.Token> tokens) {
        this.tokens = tokens;
        this.position = 0;
    }

    ////////////////////////////
    /// Main Parsing Methods ///
    ////////////////////////////

    public ParseTree.ProgramNode parse() {
        Vector<ParseTree.ClassNode> classes = new Vector<>();

        while (position < tokens.size()) {
            if (match(Tokenizer.TokenType.STOF_CLASS)) {
                classes.add(parseClass());
            } else {
                position++;
            }
        }

        return new ParseTree.ProgramNode(classes);
    }

    private ParseTree.ClassNode parseClass() {
        expect(Tokenizer.TokenType.STOF_CLASS, "Expected 'class' keyword");
        Tokenizer.Token className = expect(Tokenizer.TokenType.STOF_IDENTIFIER, "Expected class name");
        expect(Tokenizer.TokenType.STOF_OPEN_CURLY_BRACKET, "Expected '{' after class name");

        Vector<ParseTree.FunctionNode> functions = new Vector<>();
        while (!match(Tokenizer.TokenType.STOF_CLOSE_CURLY_BRACKET)) {
            functions.add(parseFunction());
        }

        expect(Tokenizer.TokenType.STOF_CLOSE_CURLY_BRACKET, "Expected '}' after class body");

        return new ParseTree.ClassNode(className.value(), functions);
    }

    private ParseTree.FunctionNode parseFunction() {
        Tokenizer.Token returnType = consume();
        String returnTypeString = returnType.type().toString();

        Tokenizer.Token functionName = expect(Tokenizer.TokenType.STOF_IDENTIFIER, "Expected function name");
        expect(Tokenizer.TokenType.STOF_OPEN_PARENTHESIS, "Expected '(' after function name");

        Vector<ParseTree.ParameterNode> parameters = new Vector<>();
        if (!match(Tokenizer.TokenType.STOF_CLOSE_PARENTHESIS)) {
            do {
                Tokenizer.Token parameterType = consume();
                Tokenizer.Token parameterName = expect(Tokenizer.TokenType.STOF_IDENTIFIER, "Expected parameter name");
                parameters.add(new ParseTree.ParameterNode(parameterType.type().toString(), parameterName.value()));

                if (match(Tokenizer.TokenType.STOF_COMMA)) {
                    consume();
                } else {
                    break;
                }
            } while (true);
        }

        expect(Tokenizer.TokenType.STOF_CLOSE_PARENTHESIS, "Expected ')' after parameters");

        ParseTree.BlockNode body = parseBlock();

        return new ParseTree.FunctionNode(returnTypeString, functionName.value(), parameters, body);
    }

    private ParseTree.BlockNode parseBlock() {
        expect(Tokenizer.TokenType.STOF_OPEN_CURLY_BRACKET, "Expected '{' to start block");

        Vector<ParseTree.StatementNode> statements = new Vector<>();
        while (!match(Tokenizer.TokenType.STOF_CLOSE_CURLY_BRACKET)) {
            statements.add(parseStatement());
        }

        expect(Tokenizer.TokenType.STOF_CLOSE_CURLY_BRACKET, "Expected '}' to end block");

        return new ParseTree.BlockNode(statements);
    }

    /////////////////////////
    /// Statement Parsing ///
    /////////////////////////

    private ParseTree.StatementNode parseStatement() {
        if (match(Tokenizer.TokenType.STOF_INT) ||
                match(Tokenizer.TokenType.STOF_BOOLEAN) ||
                match(Tokenizer.TokenType.STOF_LET) ||
                match(Tokenizer.TokenType.STOF_VAR)) {
            return parseVariableDeclaration();
        }

        if (match(Tokenizer.TokenType.STOF_RETURN)) {
            return parseReturn();
        }

        if (match(Tokenizer.TokenType.STOF_IF)) {
            return parseIf();
        }

        if (match(Tokenizer.TokenType.STOF_FOR)) {
            return parseFor();
        }

        if (match(Tokenizer.TokenType.STOF_WHILE)) {
            return parseWhile();
        }

        return parseExpressionStatement();
    }

    private ParseTree.VariableDeclarationNode parseVariableDeclaration() {
        Tokenizer.Token typeToken = consume();
        String type = typeToken.type().toString();

        Tokenizer.Token nameToken = expect(Tokenizer.TokenType.STOF_IDENTIFIER, "Expected variable name");

        ParseTree.ExpressionNode initializer = null;
        if (match(Tokenizer.TokenType.STOF_EQUALS)) {
            consume();
            initializer = parseExpression();
        }

        expect(Tokenizer.TokenType.STOF_SEMICOLON, "Expected ';' after variable declaration");

        return new ParseTree.VariableDeclarationNode(type, nameToken.value(), initializer);
    }

    private ParseTree.ReturnNode parseReturn() {
        consume();

        ParseTree.ExpressionNode value = null;
        if (!match(Tokenizer.TokenType.STOF_SEMICOLON)) {
            value = parseExpression();
        }

        expect(Tokenizer.TokenType.STOF_SEMICOLON, "Expected ';' after return statement");

        return new ParseTree.ReturnNode(value);
    }

    private ParseTree.IfNode parseIf() {
        consume();

        expect(Tokenizer.TokenType.STOF_OPEN_PARENTHESIS, "Expected '(' after 'if'");
        ParseTree.ExpressionNode condition = parseExpression();
        expect(Tokenizer.TokenType.STOF_CLOSE_PARENTHESIS, "Expected ')' after if condition");

        ParseTree.BlockNode thenBlock = parseBlock();

        Vector<ParseTree.ElseIfNode> elseIfClauses = new Vector<>();
        while (match(Tokenizer.TokenType.STOF_ELIF)) {
            consume();

            expect(Tokenizer.TokenType.STOF_OPEN_PARENTHESIS, "Expected '(' after 'elif'");
            ParseTree.ExpressionNode elifCondition = parseExpression();
            expect(Tokenizer.TokenType.STOF_CLOSE_PARENTHESIS, "Expected ')' after elif condition");

            ParseTree.BlockNode elifBlock = parseBlock();
            elseIfClauses.add(new ParseTree.ElseIfNode(elifCondition, elifBlock));
        }

        ParseTree.BlockNode elseBlock = null;
        if (match(Tokenizer.TokenType.STOF_ELSE)) {
            consume();
            elseBlock = parseBlock();
        }

        return new ParseTree.IfNode(condition, thenBlock, elseIfClauses, elseBlock);
    }

    private ParseTree.ForNode parseFor() {
        consume();

        expect(Tokenizer.TokenType.STOF_OPEN_PARENTHESIS, "Expected '(' after 'for'");

        ParseTree.StatementNode initialization = null;
        if (!match(Tokenizer.TokenType.STOF_SEMICOLON)) {
            if (match(Tokenizer.TokenType.STOF_INT) || match(Tokenizer.TokenType.STOF_BOOLEAN)) {
                initialization = parseVariableDeclaration();
            } else {
                initialization = parseExpressionStatement();
            }
        } else {
            consume();
        }

        ParseTree.ExpressionNode condition = null;
        if (!match(Tokenizer.TokenType.STOF_SEMICOLON)) {
            condition = parseExpression();
        }
        expect(Tokenizer.TokenType.STOF_SEMICOLON, "Expected ';' after for loop condition");

        ParseTree.ExpressionNode increment = null;
        if (!match(Tokenizer.TokenType.STOF_CLOSE_PARENTHESIS)) {
            increment = parseExpression();
        }
        expect(Tokenizer.TokenType.STOF_CLOSE_PARENTHESIS, "Expected ')' after for loop");

        ParseTree.BlockNode body = parseBlock();

        return new ParseTree.ForNode(initialization, condition, increment, body);
    }

    private ParseTree.WhileNode parseWhile() {
        consume();

        expect(Tokenizer.TokenType.STOF_OPEN_PARENTHESIS, "Expected '(' after 'while'");
        ParseTree.ExpressionNode condition = parseExpression();
        expect(Tokenizer.TokenType.STOF_CLOSE_PARENTHESIS, "Expected ')' after while condition");

        ParseTree.BlockNode body = parseBlock();

        return new ParseTree.WhileNode(condition, body);
    }

    private ParseTree.ExpressionStatementNode parseExpressionStatement() {
        ParseTree.ExpressionNode expression = parseExpression();
        expect(Tokenizer.TokenType.STOF_SEMICOLON, "Expected ';' after expression");
        return new ParseTree.ExpressionStatementNode(expression);
    }

    //////////////////////////
    /// Expression Parsing ///
    //////////////////////////

    private ParseTree.ExpressionNode parseExpression() {
        return parseAssignment();
    }

    private ParseTree.ExpressionNode parseAssignment() {
        ParseTree.ExpressionNode expr = parseComparison();

        if (match(Tokenizer.TokenType.STOF_EQUALS)) {
            consume();
            ParseTree.ExpressionNode value = parseAssignment();
            if (expr instanceof ParseTree.VariableNode(String name)) {
                return new ParseTree.AssignmentExpressionNode(name, value);
            }
            throw new RuntimeException("Invalid assignment target");
        }

        return expr;
    }

    private ParseTree.ExpressionNode parseComparison() {
        ParseTree.ExpressionNode expr = parseAdditive();

        while (match(Tokenizer.TokenType.STOF_GREATER_THAN) ||
                match(Tokenizer.TokenType.STOF_LESS_THAN) ||
                match(Tokenizer.TokenType.STOF_GREATER_THAN_OR_EQUAL) ||
                match(Tokenizer.TokenType.STOF_LESS_THAN_OR_EQUAL)) {
            Tokenizer.Token op = consume();
            ParseTree.ExpressionNode right = parseAdditive();
            expr = new ParseTree.BinaryOperatorNode(expr, op.toString(), right);
        }

        return expr;
    }

    private ParseTree.ExpressionNode parseAdditive() {
        ParseTree.ExpressionNode expr = parseMultiplicative();

        while (match(Tokenizer.TokenType.STOF_PLUS) ||
                match(Tokenizer.TokenType.STOF_MINUS)) {
            Tokenizer.Token op = consume();
            ParseTree.ExpressionNode right = parseMultiplicative();
            expr = new ParseTree.BinaryOperatorNode(expr, op.toString(), right);
        }

        return expr;
    }

    private ParseTree.ExpressionNode parseMultiplicative() {
        ParseTree.ExpressionNode expr = parseUnary();

        while (match(Tokenizer.TokenType.STOF_MULTIPLY) ||
                match(Tokenizer.TokenType.STOF_DIVIDE) ||
                match(Tokenizer.TokenType.STOF_MODULO)) {
            Tokenizer.Token op = consume();
            ParseTree.ExpressionNode right = parseUnary();
            expr = new ParseTree.BinaryOperatorNode(expr, op.toString(), right);
        }

        return expr;
    }

    private ParseTree.ExpressionNode parseUnary() {
        if (match(Tokenizer.TokenType.STOF_INCREMENT) ||
                match(Tokenizer.TokenType.STOF_DECREMENT)) {
            Tokenizer.Token op = consume();
            ParseTree.ExpressionNode operand = parseUnary();
            return new ParseTree.UnaryOperatorNode(op.toString(), operand, false);
        }

        return parsePostfix();
    }

    private ParseTree.ExpressionNode parsePostfix() {
        ParseTree.ExpressionNode expr = parsePrimary();

        if (match(Tokenizer.TokenType.STOF_INCREMENT) ||
                match(Tokenizer.TokenType.STOF_DECREMENT)) {
            Tokenizer.Token op = consume();
            return new ParseTree.UnaryOperatorNode(op.toString(), expr, true);
        }

        return expr;
    }

    private ParseTree.ExpressionNode parsePrimary() {
        if (match(Tokenizer.TokenType.STOF_INT_LITERAL)) {
            Tokenizer.Token token = consume();
            return new ParseTree.IntLiteralNode(Integer.parseInt(token.value()));
        }

        if (match(Tokenizer.TokenType.STOF_BOOLEAN_LITERAL)) {
            Tokenizer.Token token = consume();
            return new ParseTree.BooleanLiteralNode(token.value().equals("true"));
        }

        if (match(Tokenizer.TokenType.STOF_IDENTIFIER)) {
            Tokenizer.Token token = consume();

            if (match(Tokenizer.TokenType.STOF_OPEN_PARENTHESIS)) {
                consume();
                Vector<ParseTree.ExpressionNode> args = new Vector<>();

                if (!match(Tokenizer.TokenType.STOF_CLOSE_PARENTHESIS)) {
                    do {
                        args.add(parseExpression());
                        if (match(Tokenizer.TokenType.STOF_COMMA)) {
                            consume();
                        } else {
                            break;
                        }
                    } while (true);
                }

                expect(Tokenizer.TokenType.STOF_CLOSE_PARENTHESIS, "Expected ')' after function arguments");
                return new ParseTree.FunctionCallNode(token.value(), args);
            }

            return new ParseTree.VariableNode(token.value());
        }

        if (match(Tokenizer.TokenType.STOF_OPEN_PARENTHESIS)) {
            consume();
            ParseTree.ExpressionNode expr = parseExpression();
            expect(Tokenizer.TokenType.STOF_CLOSE_PARENTHESIS, "Expected ')' after expression");
            return expr;
        }

        throw new RuntimeException("Unexpected token in expression at position " + position);
    }

    public void writeParseTreeToFile(ParseTree.ProgramNode program, String outputPath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            writer.println("Program");
            for (ParseTree.ClassNode classNode : program.classes()) {
                writer.println("  Class: " + classNode.name());
                for (ParseTree.FunctionNode function : classNode.functions()) {
                    writer.println("    Function: " + function.returnType() + " " + function.name() + "()");
                    writer.println("      Parameters:");
                    for (ParseTree.ParameterNode param : function.parameters()) {
                        writer.println("        - " + param.type() + " " + param.name());
                    }
                    writer.println("      Body:");
                    writeBlock(writer, function.body(), 4);
                }
            }
        } catch (IOException e) {
            System.out.println("Error writing parse tree to file: " + e.getMessage());
        }
    }

    private void writeBlock(PrintWriter writer, ParseTree.BlockNode block, int indent) {
        String indentStr = "  ".repeat(indent);
        for (ParseTree.StatementNode stmt : block.statements()) {
            writer.println(indentStr + "- " + stmt.getClass().getSimpleName());
            writeStatement(writer, stmt, indent + 1);
        }
    }

    private void writeStatement(PrintWriter writer, ParseTree.StatementNode stmt, int indent) {
        String indentStr = "  ".repeat(indent);

        if (stmt instanceof ParseTree.VariableDeclarationNode(
                String type, String name, ParseTree.ExpressionNode initializer
        )) {
            writer.println(indentStr + "Type: " + type);
            writer.println(indentStr + "Name: " + name);
            if (initializer != null) {
                writer.println(indentStr + "Initializer:");
                writeExpression(writer, initializer, indent + 1);
            }
        } else if (stmt instanceof ParseTree.ReturnNode(ParseTree.ExpressionNode value)) {
            if (value != null) {
                writer.println(indentStr + "Value:");
                writeExpression(writer, value, indent + 1);
            }
        } else if (stmt instanceof ParseTree.IfNode(
                ParseTree.ExpressionNode condition, ParseTree.BlockNode thenBlock,
                java.util.List<ParseTree.ElseIfNode> elseIfClauses, ParseTree.BlockNode elseBlock
        )) {
            writer.println(indentStr + "Condition:");
            writeExpression(writer, condition, indent + 1);
            writer.println(indentStr + "Then:");
            writeBlock(writer, thenBlock, indent + 1);
            if (!elseIfClauses.isEmpty()) {
                for (ParseTree.ElseIfNode elif : elseIfClauses) {
                    writer.println(indentStr + "Elif:");
                    writeExpression(writer, elif.condition(), indent + 1);
                    writeBlock(writer, elif.block(), indent + 1);
                }
            }
            if (elseBlock != null) {
                writer.println(indentStr + "Else:");
                writeBlock(writer, elseBlock, indent + 1);
            }
        } else if (stmt instanceof ParseTree.ForNode(
                ParseTree.StatementNode initialization, ParseTree.ExpressionNode condition,
                ParseTree.ExpressionNode increment, ParseTree.BlockNode body
        )) {
            writer.println(indentStr + "Init:");
            if (initialization != null) {
                writeStatement(writer, initialization, indent + 1);
            }
            writer.println(indentStr + "Condition:");
            if (condition != null) {
                writeExpression(writer, condition, indent + 1);
            }
            writer.println(indentStr + "Increment:");
            if (increment != null) {
                writeExpression(writer, increment, indent + 1);
            }
            writer.println(indentStr + "Body:");
            writeBlock(writer, body, indent + 1);
        } else if (stmt instanceof ParseTree.WhileNode(ParseTree.ExpressionNode condition, ParseTree.BlockNode body)) {
            writer.println(indentStr + "Condition:");
            writeExpression(writer, condition, indent + 1);
            writer.println(indentStr + "Body:");
            writeBlock(writer, body, indent + 1);
        } else if (stmt instanceof ParseTree.ExpressionStatementNode(ParseTree.ExpressionNode expression)) {
            writeExpression(writer, expression, indent);
        }
    }

    private void writeExpression(PrintWriter writer, ParseTree.ExpressionNode expr, int indent) {
        String indentStr = "  ".repeat(indent);

        if (expr instanceof ParseTree.IntLiteralNode(int value)) {
            writer.println(indentStr + "IntLiteral: " + value);
        } else if (expr instanceof ParseTree.BooleanLiteralNode(boolean value)) {
            writer.println(indentStr + "BooleanLiteral: " + value);
        } else if (expr instanceof ParseTree.VariableNode(String name)) {
            writer.println(indentStr + "Variable: " + name);
        } else if (expr instanceof ParseTree.AssignmentExpressionNode(
                String variableName, ParseTree.ExpressionNode value
        )) {
            writer.println(indentStr + "Assignment: " + variableName);
            writer.println(indentStr + "  Value:");
            writeExpression(writer, value, indent + 2);
        } else if (expr instanceof ParseTree.FunctionCallNode(
                String functionName, java.util.List<ParseTree.ExpressionNode> arguments
        )) {
            writer.println(indentStr + "FunctionCall: " + functionName + "()");
            if (!arguments.isEmpty()) {
                writer.println(indentStr + "  Arguments:");
                for (ParseTree.ExpressionNode arg : arguments) {
                    writeExpression(writer, arg, indent + 2);
                }
            }
        } else if (expr instanceof ParseTree.BinaryOperatorNode(
                ParseTree.ExpressionNode left, String operator, ParseTree.ExpressionNode right
        )) {
            writer.println(indentStr + "BinaryOperator: " + operator);
            writer.println(indentStr + "  Left:");
            writeExpression(writer, left, indent + 2);
            writer.println(indentStr + "  Right:");
            writeExpression(writer, right, indent + 2);
        } else if (expr instanceof ParseTree.UnaryOperatorNode(
                String operator, ParseTree.ExpressionNode operand, boolean isPostfix
        )) {
            writer.println(indentStr + "UnaryOperator: " + operator +
                    (isPostfix ? " (postfix)" : " (prefix)"));
            writeExpression(writer, operand, indent + 1);
        }
    }

    //////////////////////
    /// Helper Methods ///
    //////////////////////

    private Tokenizer.Token peek() {
        if (position >= tokens.size()) {
            return null;
        }
        return tokens.get(position);
    }

    private Tokenizer.Token consume() {
        return tokens.get(position++);
    }

    private boolean match(Tokenizer.TokenType type) {
        Tokenizer.Token token = peek();
        return token != null && token.type() == type;
    }

    private Tokenizer.Token expect(Tokenizer.TokenType type, String message) {
        if (!match(type)) {
            throw new RuntimeException("Parse error at position " + position + ": " + message);
        }
        return consume();
    }
}