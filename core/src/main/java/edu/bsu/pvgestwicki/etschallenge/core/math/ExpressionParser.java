package edu.bsu.pvgestwicki.etschallenge.core.math;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import edu.bsu.pvgestwicki.etschallenge.core.math.Expression.Visitor;

public final class ExpressionParser {

	public static ExpressionParser create() {
		return new ExpressionParser();
	}

	public static Expression parse(List<Token> tokens) {
		return create().parseImpl(tokens);
	}

	private List<Token> tokens;
	private Stack<Node<?>> operandStack = new Stack<Node<?>>();
	private Stack<BinaryOperatorNode> operatorStack = new Stack<BinaryOperatorNode>();
	private int currentIndex = 0;
	private UnaryOperationNode pendingUnaryNodeHead = null;
	private UnaryOperationNode pendingUnaryNodeTail = null;

	public ExpressionParser() {
	}

	private Expression parseImpl(List<Token> tokens) {
		this.tokens = Lists.newLinkedList(tokens);
		return parseExpression();
	}

	private Operation operationFor(SymbolToken token) {
		switch (token) {
		case DIVIDED_BY:
			return Operation.DIVIDE;
		case TIMES:
			return Operation.MULTIPLY;
		case PLUS:
			return Operation.ADD;
		case MINUS:
			return Operation.SUBTRACT;
		default:
			throw new IllegalStateException();
		}
	}

	/**
	 * @see "http://www.smccd.net/accounts/hasson/C++2Notes/ArithmeticParsing.html"
	 */
	private Expression parseExpression() {
		boolean lookForUnaryOperations = false;
		preprocessJuxtaposedMultiplcations();
		try {
			consumeUnaryOperations();
			while (thereAreMoreTokens()) {
				if (currentTokenIsOperand()) {
					makeAndPushOperand();
					lookForUnaryOperations = false;
				} else if (lookForUnaryOperations) {
					consumeUnaryOperations();
					lookForUnaryOperations = false;
				} else {
					while (currentTokenHasLessOrEqualPrecedenceThanTopOfOperatorStack()) {
						makeBinaryOperationAndPushToOperandStack();
					}
					Operation operation = operationFor((SymbolToken) consumeToken());
					operatorStack.push(new BinaryOperatorNode(operation));
					lookForUnaryOperations = true;
				}
			}
			while (!operatorStack.isEmpty()) {
				makeBinaryOperationAndPushToOperandStack();
			}

			if (operandStack.size() != 1)
				throw new ParseException("Unexpected operand stack: "
						+ operandStack);
			if (operatorStack.size() != 0)
				throw new ParseException("Unexpected operator stack: "
						+ operatorStack);
			checkState(pendingUnaryNodeHead == null);
			checkState(pendingUnaryNodeTail == null);

			Node<?> root = operandStack.pop();

			ParseTreeExpression parsedExpression = new ParseTreeExpression(root);

			return parsedExpression;
		} catch (EmptyStackException emptyStack) {
			throw new ParseException(emptyStack);
		}
	}

	private boolean thereAreMoreTokens() {
		return currentIndex < tokens.size();
	}

	private void consumeUnaryOperations() {
		while (currentTokenIsPlusOrMinus()) {
			SymbolToken token = (SymbolToken) consumeToken();
			UnaryOperation operation = unaryOperationFor(token);
			UnaryOperationNode node = new UnaryOperationNode(operation);
			setOrAppendUnaryOperationNode(node);
		}
	}

	private void setOrAppendUnaryOperationNode(UnaryOperationNode node) {
		if (pendingUnaryNodeHead == null)
			pendingUnaryNodeHead = pendingUnaryNodeTail = node;
		else {
			pendingUnaryNodeTail.operand = node;
			pendingUnaryNodeTail = node;
		}
	}

	private UnaryOperation unaryOperationFor(SymbolToken token) {
		switch (token) {
		case PLUS:
			return UnaryOperation.POSITIVIZE;
		case MINUS:
			return UnaryOperation.NEGATE;
		default:
			throw new IllegalArgumentException(token.toString());
		}
	}

	private boolean currentTokenIsPlusOrMinus() {
		return currentToken().accept(plusOrMinusDiscriminator);
	}

	private void makeBinaryOperationAndPushToOperandStack() {
		BinaryOperatorNode operatorNode = operatorStack.pop();
		operatorNode.right = operandStack.pop();
		operatorNode.left = operandStack.pop();

		operandStack.push(operatorNode);
	}

	private void makeAndPushOperand() {
		Token token = consumeToken();
		Node<?> node = token.accept(operandNodeFactory);
		node = processPendingUnaryOperations(node);
		operandStack.push(node);
	}

	private Node<?> processPendingUnaryOperations(Node<?> node) {
		if (pendingUnaryNodeHead != null) {
			Node<?> result = pendingUnaryNodeHead;
			pendingUnaryNodeTail.operand = node;
			pendingUnaryNodeHead = pendingUnaryNodeTail = null;
			return result;
		} else
			return node;
	}

	private void preprocessJuxtaposedMultiplcations() {
		for (int i = 0; i < tokens.size(); i++) {
			if (isJuxtaposedMultiplicationAt(i)) {
				tokens.add(i + 1, SymbolToken.TIMES);
				i++;
			}
		}
	}

	private boolean isJuxtaposedMultiplicationAt(int i) {
		if (i + 1 < tokens.size()) {
			Token t1 = tokens.get(i);
			Token t2 = tokens.get(i + 1);
			return isJuxtaposedMultiplication(t1, t2);
		}
		return false;
	}

	private boolean isJuxtaposedMultiplication(Token t1, Token t2) {
		if (oneIsAVariable(t1, t2)) {
			return t1.accept(integerOrVariableDiscriminator)
					&& t2.accept(integerOrVariableDiscriminator);
		} else
			return false;
	}

	private boolean currentTokenIsOperand() {
		return currentToken().accept(isOperand);
	}

	private Token consumeToken() {
		Token token = tokens.get(currentIndex);
		currentIndex++;
		return token;
	}

	private boolean oneIsAVariable(Token t1, Token t2) {
		return t1.accept(variableDiscriminator)
				|| t2.accept(variableDiscriminator);
	}

	private boolean currentTokenHasLessOrEqualPrecedenceThanTopOfOperatorStack() {
		if (operatorStack.isEmpty())
			return false;
		else {
			Operation topOperation = operatorStack.peek().data;
			Operation currentOperation = operationFor((SymbolToken) currentToken());
			return !currentOperation.isHigherPriorityThan(topOperation);
		}
	}

	private Token currentToken() {
		return tokens.get(currentIndex);
	}

	private static final TokenVisitor<Boolean> integerOrVariableDiscriminator = new TokenVisitor<Boolean>() {
		@Override
		public Boolean visit(IntegerToken integerToken) {
			return true;
		}

		@Override
		public Boolean visit(SymbolToken symbolToken) {
			return false;
		}

		@Override
		public Boolean visit(VariableToken variableToken) {
			return true;
		}
	};

	private static final TokenVisitor<Boolean> variableDiscriminator = new TokenVisitor<Boolean>() {

		@Override
		public Boolean visit(IntegerToken integerToken) {
			return false;
		}

		@Override
		public Boolean visit(SymbolToken symbolToken) {
			return false;
		}

		@Override
		public Boolean visit(VariableToken variableToken) {
			return true;
		}

	};

	private static final TokenVisitor<Boolean> plusOrMinusDiscriminator = new TokenVisitor<Boolean>() {

		@Override
		public Boolean visit(IntegerToken integerToken) {
			return false;
		}

		@Override
		public Boolean visit(SymbolToken symbolToken) {
			switch (symbolToken) {
			case PLUS:
			case MINUS:
				return true;
			default:
				return false;
			}
		}

		@Override
		public Boolean visit(VariableToken variableToken) {
			return false;
		}

	};

	private static abstract class Node<T> {
		T data;

		protected Node(T data) {
			this.data = checkNotNull(data);
		}

		public abstract int value(Environment env);

		public abstract void accept(Visitor v);

		@Override
		public int hashCode() {
			return Objects.hashCode(data);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null)
				return false;
			if (obj.getClass().equals(getClass())) {
				Node<?> other = (Node<?>) obj;
				return Objects.equal(this.data, other.data);
			}
			return false;
		}

		@Override
		public String toString() {
			return Objects.toStringHelper(this).add("data", data).toString();
		}

	}

	private static final class NumberNode extends Node<Integer> {
		public NumberNode(int data) {
			super(data);
		}

		@Override
		public int value(Environment env) {
			return data;
		}

		@Override
		public void accept(Visitor v) {
			v.visit(data);
		}
	}

	private static final class VariableNode extends Node<Variable> {
		public VariableNode(Variable v) {
			super(v);
		}

		@Override
		public int value(Environment env) {
			return env.valueOf(data);
		}

		@Override
		public void accept(Visitor v) {
			v.visit(data);
		}
	}

	private static final class BinaryOperatorNode extends Node<Operation> {
		Node<?> left;
		Node<?> right;

		public BinaryOperatorNode(Operation operator) {
			super(operator);
		}

		@Override
		public int value(Environment env) {
			switch (data) {
			case ADD:
				return left.value(env) + right.value(env);
			case SUBTRACT:
				return left.value(env) - right.value(env);
			case MULTIPLY:
				return left.value(env) * right.value(env);
			case DIVIDE:
				return left.value(env) / right.value(env);
			default:
				throw new IllegalStateException();
			}
		}

		@Override
		public void accept(Visitor v) {
			left.accept(v);
			v.visit(data);
			right.accept(v);
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(data, left, right);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof BinaryOperatorNode) {
				BinaryOperatorNode other = (BinaryOperatorNode) obj;
				return Objects.equal(data, other.data)
						&& Objects.equal(left, other.left)
						&& Objects.equal(right, other.right);
			}
			return false;
		}

		@Override
		public String toString() {
			return Objects.toStringHelper(this).add("data", data)
					.add("left", left).add("right", right).toString();
		}
	}

	private static final class UnaryOperationNode extends Node<UnaryOperation> {

		Node<?> operand;

		public UnaryOperationNode(UnaryOperation op) {
			super(op);
		}

		@Override
		public int value(Environment env) {
			switch (data) {
			case POSITIVIZE:
				return operand.value(env);
			case NEGATE:
				return -operand.value(env);
			default:
				throw new IllegalStateException();
			}
		}

		@Override
		public void accept(Visitor v) {
			v.visit(data);
			operand.accept(v);
		}
	}

	private static final class ParseTreeExpression implements Expression {
		private final Node<?> root;

		public ParseTreeExpression(Node<?> root) {
			this.root = root;
		}

		@Override
		public int value(Environment env) {
			return root.value(env);
		}

		@Override
		public void accept(Expression.Visitor visitor) {
			root.accept(visitor);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ParseTreeExpression) {
				ParseTreeExpression other = (ParseTreeExpression) obj;
				return Objects.equal(root, other.root);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(root);
		}

		@Override
		public String toString() {
			return Objects.toStringHelper(this).add("root", root).toString();
		}

	}

	private static final TokenVisitor<Boolean> isOperand = new TokenVisitor<Boolean>() {

		@Override
		public Boolean visit(IntegerToken integerToken) {
			return true;
		}

		@Override
		public Boolean visit(SymbolToken symbolToken) {
			return false;
		}

		@Override
		public Boolean visit(VariableToken variableToken) {
			return true;
		}
	};

	private static final TokenVisitor<Node<?>> operandNodeFactory = new TokenVisitor<ExpressionParser.Node<?>>() {

		@Override
		public Node<?> visit(IntegerToken integerToken) {
			return new NumberNode(integerToken.value());
		}

		@Override
		public Node<?> visit(SymbolToken symbolToken) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Node<?> visit(VariableToken variableToken) {
			Variable v = VariableBuilder.create(variableToken.name());
			return new VariableNode(v);
		}

	};
}
