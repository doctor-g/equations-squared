package edu.bsu.pvgestwicki.etschallenge.core.math;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class OperationExtractor extends Expression.Visitor.Adapter {

	private final List<Operation> operations = Lists.newArrayList();

	@Override
	public void visit(Operation operator) {
		operations.add(operator);
	}

	public List<Operation> operations() {
		return ImmutableList.copyOf(operations);
	}

}
