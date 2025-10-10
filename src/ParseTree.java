import java.util.List;

public class ParseTree {

    //////////////////////////
    /// Utility Interfaces ///
    //////////////////////////
    // base node interface
    public sealed interface Node permits StatementNode, ExpressionNode {}
    // interface for all statement nodes
    public sealed interface StatementNode extends Node permits VariableDeclarationNode, VariableAssignmentNode, IfNode, ForNode, WhileNode, ReturnNode, ExitNode, ExpressionStatementNode {}
    // interface for all expression nodes
    public sealed interface ExpressionNode extends Node permits VariableNode, BinaryOperatorNode, UnaryOperatorNode, IntLiteralNode, BooleanLiteralNode, FunctionCallNode, AssignmentExpressionNode {}
    public record AssignmentExpressionNode(String variableName, ExpressionNode value) implements ExpressionNode {}

    ////////////////////////////////////
    /// Program Structure Definition ///
    ////////////////////////////////////
    public record ProgramNode(List<ClassNode> classes) {}

    public record ClassNode(String name, List<FunctionNode> functions) {}

    public record FunctionNode(String returnType, String name, List<ParameterNode> parameters, BlockNode body) {}

    public record ParameterNode(String type, String name) {}

    public record BlockNode(List<StatementNode> statements) {}

    ///////////////////////
    /// Statement Nodes ///
    ///////////////////////
    public record VariableDeclarationNode(String type, String name, ExpressionNode initializer) implements StatementNode {}
    public record VariableAssignmentNode(String name, ExpressionNode value) implements StatementNode {}
    public record IfNode(ExpressionNode condition, BlockNode thenBlock, List<ElseIfNode> elseIfClauses, BlockNode elseBlock) implements StatementNode {}
    public record ElseIfNode(ExpressionNode condition, BlockNode block) {}
    public record ForNode(StatementNode initialization, ExpressionNode condition, ExpressionNode increment, BlockNode body) implements StatementNode {}
    public record WhileNode(ExpressionNode condition, BlockNode body) implements StatementNode {}
    public record ReturnNode(ExpressionNode value) implements StatementNode {}
    public record ExitNode(ExpressionNode exitCode) implements StatementNode {}
    public record ExpressionStatementNode(ExpressionNode expression) implements StatementNode {}

    ////////////////////////
    /// Expression Nodes ///
    ////////////////////////
    public record VariableNode(String name) implements ExpressionNode {}
    public record BinaryOperatorNode(ExpressionNode left, String operator, ExpressionNode right) implements ExpressionNode {}
    public record UnaryOperatorNode(String operator, ExpressionNode operand, boolean isPostfix) implements ExpressionNode {}
    public record IntLiteralNode(int value) implements ExpressionNode {}
    public record BooleanLiteralNode(boolean value) implements ExpressionNode {}
    public record FunctionCallNode(String functionName, List<ExpressionNode> arguments) implements ExpressionNode {}
}