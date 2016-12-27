package main.operator;

import java.util.function.Predicate;

import main.OneMove;

/**
 * Created by lzarczynski on 27.12.2016.
 */
public class ForwardOrHorizontal extends Operator {
  private static final Operator instance = new ForwardOrHorizontal();

  public static Operator instance() {
    return instance;
  }

  public static Operator instance(int priority) {
    instance.priority = priority;
    return instance;
  }

  @Override
  public Predicate<OneMove> matches() {
    return Forward.instance(1).matches().or(Horizontal.instance(1).matches());
  }

  @Override
  public String getDescription() {
    return "Forward or Horizontal";
  }
}
