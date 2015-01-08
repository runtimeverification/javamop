package mop;
public aspect BaseAspect {
	pointcut notwithin() :
	!within(*HasNext*);
}
