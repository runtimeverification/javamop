public aspect BaseAspect {
	pointcut notwithin() :
	!within(*HasNext*);
}
