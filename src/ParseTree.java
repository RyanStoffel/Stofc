import java.util.List;

public class ParseTree {

    // =============================
    // BASE INTERFACES
    // =============================

    public sealed interface Node permits StatementNode, ExpressionNode {}

    public sealed interface StatementNode extends Node
            permits VariableDeclarationNode, VariableAssignmentNode, IfNode,
            ForNode, WhileNode, ReturnNode, ExitNode, ExpressionStatementNode {}

    public sealed interface ExpressionNode extends Node
            permits VariableNode, BinaryOperatorNode, UnaryOperatorNode,
            IntLiteralNode, BooleanLiteralNode, FunctionCallNode, AssignmentExpressionNode {}

    public record AssignmentExpressionNode(String variableName, ExpressionNode value) implements ExpressionNode {}

    // =============================
    // PROGRAM STRUCTURE
    // =============================

    public record ProgramNode(List<ClassNode> classes) {}

    public record ClassNode(String name, List<FunctionNode> functions) {}

    public record FunctionNode(String returnType,
                               String name,
                               List<ParameterNode> parameters,
                               BlockNode body) {}

    public record ParameterNode(String type, String name) {}

    public record BlockNode(List<StatementNode> statements) {}

    // =============================
    // STATEMENT NODES
    // =============================

    // Variable declaration: int i = 5;
    public record VariableDeclarationNode(String type,
                                          String name,
                                          ExpressionNode initializer) implements StatementNode {}
    // initializer can be null

    // Variable assignment: i = 10;
    public record VariableAssignmentNode(String name,
                                         ExpressionNode value) implements StatementNode {}

    // if (condition) { thenBlock } elif ... else { elseBlock }
    public record IfNode(ExpressionNode condition,
                         BlockNode thenBlock,
                         List<ElseIfNode> elseIfClauses,
                         BlockNode elseBlock) implements StatementNode {}
    // elseIfClauses can be empty, elseBlock can be null

    // Helper for elif clauses (not a statement itself)
    public record ElseIfNode(ExpressionNode condition, BlockNode block) {}

    // for (init; condition; increment) { body }
    public record ForNode(StatementNode initialization,
                          ExpressionNode condition,
                          ExpressionNode increment,
                          BlockNode body) implements StatementNode {}
    // initialization, condition, and increment can be null

    // while (condition) { body }
    public record WhileNode(ExpressionNode condition,
                            BlockNode body) implements StatementNode {}

    // return value;
    public record ReturnNode(ExpressionNode value) implements StatementNode {}
    // value can be null for "return;"

    // exit 0;
    public record ExitNode(ExpressionNode exitCode) implements StatementNode {}

    // Expression as a statement (e.g., function calls, i++;)
    public record ExpressionStatementNode(ExpressionNode expression) implements StatementNode {}

    // =============================
    // EXPRESSION NODES
    // =============================

    // Variable reference: i, x, myVar
    public record VariableNode(String name) implements ExpressionNode {}

    // Binary operations: a + b, x > y, etc.
    public record BinaryOperatorNode(ExpressionNode left,
                                     String operator,
                                     ExpressionNode right) implements ExpressionNode {}
    // operator: "+", "-", "*", "/", "%", ">", "<", ">=", "<=", "=="

    // Unary operations: i++, ++i, i--, --i
    public record UnaryOperatorNode(String operator,
                                    ExpressionNode operand,
                                    boolean isPostfix) implements ExpressionNode {}
    // operator: "++" or "--"
    // isPostfix: true for i++, false for ++i

    // Integer literal: 5, 42, 0
    public record IntLiteralNode(int value) implements ExpressionNode {}


    // Boolean literal: true, false
    public record BooleanLiteralNode(boolean value) implements ExpressionNode {}

    // Function call: print(x), calculateSum(1, 2, 3)
    public record FunctionCallNode(String functionName,
                                   List<ExpressionNode> arguments) implements ExpressionNode {}
    // arguments can be empty
}