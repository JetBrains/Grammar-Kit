%%

%eof{ /*no code same line*/return;
%eof}

%eofthrow{
%eofthrow}

NO_BRACK_IN_CLASS=[^[]
NO_MACRO_IN_LINE=abc=d
UNCLOSED_CLASS = [

DIGITS={DIGIT}+
#if($dialect == '111' || $dialect == '222')
NUMBER_PREFIX=[]
FLOAT_POSTFIX=[dDfF]
#end

NEWLINES=A
 "a"
 [:digit:]
 [a]
 (A
 | B)
 BAD

%%