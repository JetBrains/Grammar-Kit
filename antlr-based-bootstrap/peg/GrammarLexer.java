/*
 * Copyright 2011-2011 Gregory Shrago
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// $ANTLR 3.3 Nov 30, 2010 12:45:30 /Users/gregory/Projects/PEG/Grammar.g 2011-07-15 05:33:29

package peg;


import org.antlr.runtime.*;

public class GrammarLexer extends Lexer {
    public static final int EOF=-1;
    public static final int T__24=24;
    public static final int T__25=25;
    public static final int T__26=26;
    public static final int T__27=27;
    public static final int T__28=28;
    public static final int T__29=29;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int RULE=4;
    public static final int ATTR=5;
    public static final int SEQ=6;
    public static final int CHOICE=7;
    public static final int ATTRS=8;
    public static final int MODIFIER=9;
    public static final int ID=10;
    public static final int COMMENT=11;
    public static final int WS=12;
    public static final int ESC=13;
    public static final int STRING=14;
    public static final int XDIGIT=15;
    public static final int DIGIT=16;
    public static final int NUMBER=17;
    public static final int OR=18;
    public static final int AND=19;
    public static final int NOT=20;
    public static final int OPT=21;
    public static final int ONEMORE=22;
    public static final int ZEROMORE=23;

    // delegates
    // delegators

    public GrammarLexer() {;} 
    public GrammarLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public GrammarLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    public String getGrammarFileName() { return "/Users/gregory/Projects/PEG/Grammar.g"; }

    // $ANTLR start "T__24"
    public final void mT__24() throws RecognitionException {
        try {
            int _type = T__24;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/gregory/Projects/PEG/Grammar.g:11:7: ( '::=' )
            // /Users/gregory/Projects/PEG/Grammar.g:11:9: '::='
            {
            match("::="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__24"

    // $ANTLR start "T__25"
    public final void mT__25() throws RecognitionException {
        try {
            int _type = T__25;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/gregory/Projects/PEG/Grammar.g:12:7: ( ';' )
            // /Users/gregory/Projects/PEG/Grammar.g:12:9: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__25"

    // $ANTLR start "T__26"
    public final void mT__26() throws RecognitionException {
        try {
            int _type = T__26;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/gregory/Projects/PEG/Grammar.g:13:7: ( 'private' )
            // /Users/gregory/Projects/PEG/Grammar.g:13:9: 'private'
            {
            match("private"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__26"

    // $ANTLR start "T__27"
    public final void mT__27() throws RecognitionException {
        try {
            int _type = T__27;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/gregory/Projects/PEG/Grammar.g:14:7: ( '{' )
            // /Users/gregory/Projects/PEG/Grammar.g:14:9: '{'
            {
            match('{'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__27"

    // $ANTLR start "T__28"
    public final void mT__28() throws RecognitionException {
        try {
            int _type = T__28;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/gregory/Projects/PEG/Grammar.g:15:7: ( '}' )
            // /Users/gregory/Projects/PEG/Grammar.g:15:9: '}'
            {
            match('}'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__28"

    // $ANTLR start "T__29"
    public final void mT__29() throws RecognitionException {
        try {
            int _type = T__29;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/gregory/Projects/PEG/Grammar.g:16:7: ( '=' )
            // /Users/gregory/Projects/PEG/Grammar.g:16:9: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__29"

    // $ANTLR start "T__30"
    public final void mT__30() throws RecognitionException {
        try {
            int _type = T__30;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/gregory/Projects/PEG/Grammar.g:17:7: ( '(' )
            // /Users/gregory/Projects/PEG/Grammar.g:17:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__30"

    // $ANTLR start "T__31"
    public final void mT__31() throws RecognitionException {
        try {
            int _type = T__31;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/gregory/Projects/PEG/Grammar.g:18:7: ( ')' )
            // /Users/gregory/Projects/PEG/Grammar.g:18:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "T__31"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/gregory/Projects/PEG/Grammar.g:30:5: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
            // /Users/gregory/Projects/PEG/Grammar.g:30:7: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // /Users/gregory/Projects/PEG/Grammar.g:30:31: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='0' && LA1_0<='9')||(LA1_0>='A' && LA1_0<='Z')||LA1_0=='_'||(LA1_0>='a' && LA1_0<='z')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/gregory/Projects/PEG/Grammar.g:
            	    {
            	    if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "COMMENT"
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/gregory/Projects/PEG/Grammar.g:34:5: ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? ( '\\n' )? | '/*' ( options {greedy=false; } : . )* '*/' )
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0=='/') ) {
                int LA6_1 = input.LA(2);

                if ( (LA6_1=='/') ) {
                    alt6=1;
                }
                else if ( (LA6_1=='*') ) {
                    alt6=2;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 6, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }
            switch (alt6) {
                case 1 :
                    // /Users/gregory/Projects/PEG/Grammar.g:34:9: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? ( '\\n' )?
                    {
                    match("//"); 

                    // /Users/gregory/Projects/PEG/Grammar.g:34:14: (~ ( '\\n' | '\\r' ) )*
                    loop2:
                    do {
                        int alt2=2;
                        int LA2_0 = input.LA(1);

                        if ( ((LA2_0>='\u0000' && LA2_0<='\t')||(LA2_0>='\u000B' && LA2_0<='\f')||(LA2_0>='\u000E' && LA2_0<='\uFFFF')) ) {
                            alt2=1;
                        }


                        switch (alt2) {
                    	case 1 :
                    	    // /Users/gregory/Projects/PEG/Grammar.g:34:14: ~ ( '\\n' | '\\r' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||(input.LA(1)>='\u000B' && input.LA(1)<='\f')||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop2;
                        }
                    } while (true);

                    // /Users/gregory/Projects/PEG/Grammar.g:34:28: ( '\\r' )?
                    int alt3=2;
                    int LA3_0 = input.LA(1);

                    if ( (LA3_0=='\r') ) {
                        alt3=1;
                    }
                    switch (alt3) {
                        case 1 :
                            // /Users/gregory/Projects/PEG/Grammar.g:34:28: '\\r'
                            {
                            match('\r'); 

                            }
                            break;

                    }

                    // /Users/gregory/Projects/PEG/Grammar.g:34:34: ( '\\n' )?
                    int alt4=2;
                    int LA4_0 = input.LA(1);

                    if ( (LA4_0=='\n') ) {
                        alt4=1;
                    }
                    switch (alt4) {
                        case 1 :
                            // /Users/gregory/Projects/PEG/Grammar.g:34:34: '\\n'
                            {
                            match('\n'); 

                            }
                            break;

                    }

                    _channel=HIDDEN;

                    }
                    break;
                case 2 :
                    // /Users/gregory/Projects/PEG/Grammar.g:35:9: '/*' ( options {greedy=false; } : . )* '*/'
                    {
                    match("/*"); 

                    // /Users/gregory/Projects/PEG/Grammar.g:35:14: ( options {greedy=false; } : . )*
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0=='*') ) {
                            int LA5_1 = input.LA(2);

                            if ( (LA5_1=='/') ) {
                                alt5=2;
                            }
                            else if ( ((LA5_1>='\u0000' && LA5_1<='.')||(LA5_1>='0' && LA5_1<='\uFFFF')) ) {
                                alt5=1;
                            }


                        }
                        else if ( ((LA5_0>='\u0000' && LA5_0<=')')||(LA5_0>='+' && LA5_0<='\uFFFF')) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // /Users/gregory/Projects/PEG/Grammar.g:35:42: .
                    	    {
                    	    matchAny(); 

                    	    }
                    	    break;

                    	default :
                    	    break loop5;
                        }
                    } while (true);

                    match("*/"); 

                    _channel=HIDDEN;

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMENT"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/gregory/Projects/PEG/Grammar.g:38:5: ( ( ' ' | '\\t' | '\\r' | '\\n' ) )
            // /Users/gregory/Projects/PEG/Grammar.g:38:9: ( ' ' | '\\t' | '\\r' | '\\n' )
            {
            if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/gregory/Projects/PEG/Grammar.g:48:2: ( '\\'' ( ESC | ~ ( '\\'' | '\\\\' ) )* '\\'' | '\"' ( ESC | ~ ( '\\\\' | '\"' ) )* '\"' )
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0=='\'') ) {
                alt9=1;
            }
            else if ( (LA9_0=='\"') ) {
                alt9=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // /Users/gregory/Projects/PEG/Grammar.g:48:4: '\\'' ( ESC | ~ ( '\\'' | '\\\\' ) )* '\\''
                    {
                    match('\''); 
                    // /Users/gregory/Projects/PEG/Grammar.g:48:9: ( ESC | ~ ( '\\'' | '\\\\' ) )*
                    loop7:
                    do {
                        int alt7=3;
                        int LA7_0 = input.LA(1);

                        if ( (LA7_0=='\\') ) {
                            alt7=1;
                        }
                        else if ( ((LA7_0>='\u0000' && LA7_0<='&')||(LA7_0>='(' && LA7_0<='[')||(LA7_0>=']' && LA7_0<='\uFFFF')) ) {
                            alt7=2;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // /Users/gregory/Projects/PEG/Grammar.g:48:10: ESC
                    	    {
                    	    mESC(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // /Users/gregory/Projects/PEG/Grammar.g:48:14: ~ ( '\\'' | '\\\\' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='&')||(input.LA(1)>='(' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);

                    match('\''); 

                    }
                    break;
                case 2 :
                    // /Users/gregory/Projects/PEG/Grammar.g:49:4: '\"' ( ESC | ~ ( '\\\\' | '\"' ) )* '\"'
                    {
                    match('\"'); 
                    // /Users/gregory/Projects/PEG/Grammar.g:49:8: ( ESC | ~ ( '\\\\' | '\"' ) )*
                    loop8:
                    do {
                        int alt8=3;
                        int LA8_0 = input.LA(1);

                        if ( (LA8_0=='\\') ) {
                            alt8=1;
                        }
                        else if ( ((LA8_0>='\u0000' && LA8_0<='!')||(LA8_0>='#' && LA8_0<='[')||(LA8_0>=']' && LA8_0<='\uFFFF')) ) {
                            alt8=2;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // /Users/gregory/Projects/PEG/Grammar.g:49:9: ESC
                    	    {
                    	    mESC(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // /Users/gregory/Projects/PEG/Grammar.g:49:15: ~ ( '\\\\' | '\"' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop8;
                        }
                    } while (true);

                    match('\"'); 

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "ESC"
    public final void mESC() throws RecognitionException {
        try {
            // /Users/gregory/Projects/PEG/Grammar.g:52:5: ( '\\\\' ( 'n' | 'r' | 't' | 'b' | 'f' | '\"' | '\\'' | '\\\\' | '>' | 'u' XDIGIT XDIGIT XDIGIT XDIGIT | . ) )
            // /Users/gregory/Projects/PEG/Grammar.g:52:7: '\\\\' ( 'n' | 'r' | 't' | 'b' | 'f' | '\"' | '\\'' | '\\\\' | '>' | 'u' XDIGIT XDIGIT XDIGIT XDIGIT | . )
            {
            match('\\'); 
            // /Users/gregory/Projects/PEG/Grammar.g:53:3: ( 'n' | 'r' | 't' | 'b' | 'f' | '\"' | '\\'' | '\\\\' | '>' | 'u' XDIGIT XDIGIT XDIGIT XDIGIT | . )
            int alt10=11;
            alt10 = dfa10.predict(input);
            switch (alt10) {
                case 1 :
                    // /Users/gregory/Projects/PEG/Grammar.g:53:5: 'n'
                    {
                    match('n'); 

                    }
                    break;
                case 2 :
                    // /Users/gregory/Projects/PEG/Grammar.g:54:5: 'r'
                    {
                    match('r'); 

                    }
                    break;
                case 3 :
                    // /Users/gregory/Projects/PEG/Grammar.g:55:5: 't'
                    {
                    match('t'); 

                    }
                    break;
                case 4 :
                    // /Users/gregory/Projects/PEG/Grammar.g:56:5: 'b'
                    {
                    match('b'); 

                    }
                    break;
                case 5 :
                    // /Users/gregory/Projects/PEG/Grammar.g:57:5: 'f'
                    {
                    match('f'); 

                    }
                    break;
                case 6 :
                    // /Users/gregory/Projects/PEG/Grammar.g:58:5: '\"'
                    {
                    match('\"'); 

                    }
                    break;
                case 7 :
                    // /Users/gregory/Projects/PEG/Grammar.g:59:5: '\\''
                    {
                    match('\''); 

                    }
                    break;
                case 8 :
                    // /Users/gregory/Projects/PEG/Grammar.g:60:5: '\\\\'
                    {
                    match('\\'); 

                    }
                    break;
                case 9 :
                    // /Users/gregory/Projects/PEG/Grammar.g:61:5: '>'
                    {
                    match('>'); 

                    }
                    break;
                case 10 :
                    // /Users/gregory/Projects/PEG/Grammar.g:62:5: 'u' XDIGIT XDIGIT XDIGIT XDIGIT
                    {
                    match('u'); 
                    mXDIGIT(); 
                    mXDIGIT(); 
                    mXDIGIT(); 
                    mXDIGIT(); 

                    }
                    break;
                case 11 :
                    // /Users/gregory/Projects/PEG/Grammar.g:63:5: .
                    {
                    matchAny(); 

                    }
                    break;

            }


            }

        }
        finally {
        }
    }
    // $ANTLR end "ESC"

    // $ANTLR start "DIGIT"
    public final void mDIGIT() throws RecognitionException {
        try {
            // /Users/gregory/Projects/PEG/Grammar.g:68:8: ( '0' .. '9' )
            // /Users/gregory/Projects/PEG/Grammar.g:68:10: '0' .. '9'
            {
            matchRange('0','9'); 

            }

        }
        finally {
        }
    }
    // $ANTLR end "DIGIT"

    // $ANTLR start "XDIGIT"
    public final void mXDIGIT() throws RecognitionException {
        try {
            int _type = XDIGIT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/gregory/Projects/PEG/Grammar.g:69:8: ( | 'a' .. 'f' | 'A' .. 'F' )
            int alt11=3;
            switch ( input.LA(1) ) {
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
                {
                alt11=2;
                }
                break;
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
                {
                alt11=3;
                }
                break;
            default:
                alt11=1;}

            switch (alt11) {
                case 1 :
                    // /Users/gregory/Projects/PEG/Grammar.g:70:2: 
                    {
                    }
                    break;
                case 2 :
                    // /Users/gregory/Projects/PEG/Grammar.g:70:4: 'a' .. 'f'
                    {
                    matchRange('a','f'); 

                    }
                    break;
                case 3 :
                    // /Users/gregory/Projects/PEG/Grammar.g:71:4: 'A' .. 'F'
                    {
                    matchRange('A','F'); 

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "XDIGIT"

    // $ANTLR start "NUMBER"
    public final void mNUMBER() throws RecognitionException {
        try {
            int _type = NUMBER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/gregory/Projects/PEG/Grammar.g:73:9: ( ( DIGIT )+ )
            // /Users/gregory/Projects/PEG/Grammar.g:73:11: ( DIGIT )+
            {
            // /Users/gregory/Projects/PEG/Grammar.g:73:11: ( DIGIT )+
            int cnt12=0;
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( ((LA12_0>='0' && LA12_0<='9')) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // /Users/gregory/Projects/PEG/Grammar.g:73:11: DIGIT
            	    {
            	    mDIGIT(); 

            	    }
            	    break;

            	default :
            	    if ( cnt12 >= 1 ) break loop12;
                        EarlyExitException eee =
                            new EarlyExitException(12, input);
                        throw eee;
                }
                cnt12++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NUMBER"

    // $ANTLR start "OR"
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/gregory/Projects/PEG/Grammar.g:74:4: ( '|' )
            // /Users/gregory/Projects/PEG/Grammar.g:74:6: '|'
            {
            match('|'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OR"

    // $ANTLR start "AND"
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/gregory/Projects/PEG/Grammar.g:75:5: ( '&' )
            // /Users/gregory/Projects/PEG/Grammar.g:75:7: '&'
            {
            match('&'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AND"

    // $ANTLR start "NOT"
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/gregory/Projects/PEG/Grammar.g:76:5: ( '!' )
            // /Users/gregory/Projects/PEG/Grammar.g:76:7: '!'
            {
            match('!'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NOT"

    // $ANTLR start "OPT"
    public final void mOPT() throws RecognitionException {
        try {
            int _type = OPT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/gregory/Projects/PEG/Grammar.g:77:5: ( '?' )
            // /Users/gregory/Projects/PEG/Grammar.g:77:7: '?'
            {
            match('?'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OPT"

    // $ANTLR start "ONEMORE"
    public final void mONEMORE() throws RecognitionException {
        try {
            int _type = ONEMORE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/gregory/Projects/PEG/Grammar.g:78:9: ( '+' )
            // /Users/gregory/Projects/PEG/Grammar.g:78:11: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ONEMORE"

    // $ANTLR start "ZEROMORE"
    public final void mZEROMORE() throws RecognitionException {
        try {
            int _type = ZEROMORE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /Users/gregory/Projects/PEG/Grammar.g:79:9: ( '*' )
            // /Users/gregory/Projects/PEG/Grammar.g:79:11: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ZEROMORE"

    public void mTokens() throws RecognitionException {
        // /Users/gregory/Projects/PEG/Grammar.g:1:8: ( T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | ID | COMMENT | WS | STRING | XDIGIT | NUMBER | OR | AND | NOT | OPT | ONEMORE | ZEROMORE )
        int alt13=20;
        alt13 = dfa13.predict(input);
        switch (alt13) {
            case 1 :
                // /Users/gregory/Projects/PEG/Grammar.g:1:10: T__24
                {
                mT__24(); 

                }
                break;
            case 2 :
                // /Users/gregory/Projects/PEG/Grammar.g:1:16: T__25
                {
                mT__25(); 

                }
                break;
            case 3 :
                // /Users/gregory/Projects/PEG/Grammar.g:1:22: T__26
                {
                mT__26(); 

                }
                break;
            case 4 :
                // /Users/gregory/Projects/PEG/Grammar.g:1:28: T__27
                {
                mT__27(); 

                }
                break;
            case 5 :
                // /Users/gregory/Projects/PEG/Grammar.g:1:34: T__28
                {
                mT__28(); 

                }
                break;
            case 6 :
                // /Users/gregory/Projects/PEG/Grammar.g:1:40: T__29
                {
                mT__29(); 

                }
                break;
            case 7 :
                // /Users/gregory/Projects/PEG/Grammar.g:1:46: T__30
                {
                mT__30(); 

                }
                break;
            case 8 :
                // /Users/gregory/Projects/PEG/Grammar.g:1:52: T__31
                {
                mT__31(); 

                }
                break;
            case 9 :
                // /Users/gregory/Projects/PEG/Grammar.g:1:58: ID
                {
                mID(); 

                }
                break;
            case 10 :
                // /Users/gregory/Projects/PEG/Grammar.g:1:61: COMMENT
                {
                mCOMMENT(); 

                }
                break;
            case 11 :
                // /Users/gregory/Projects/PEG/Grammar.g:1:69: WS
                {
                mWS(); 

                }
                break;
            case 12 :
                // /Users/gregory/Projects/PEG/Grammar.g:1:72: STRING
                {
                mSTRING(); 

                }
                break;
            case 13 :
                // /Users/gregory/Projects/PEG/Grammar.g:1:79: XDIGIT
                {
                mXDIGIT(); 

                }
                break;
            case 14 :
                // /Users/gregory/Projects/PEG/Grammar.g:1:86: NUMBER
                {
                mNUMBER(); 

                }
                break;
            case 15 :
                // /Users/gregory/Projects/PEG/Grammar.g:1:93: OR
                {
                mOR(); 

                }
                break;
            case 16 :
                // /Users/gregory/Projects/PEG/Grammar.g:1:96: AND
                {
                mAND(); 

                }
                break;
            case 17 :
                // /Users/gregory/Projects/PEG/Grammar.g:1:100: NOT
                {
                mNOT(); 

                }
                break;
            case 18 :
                // /Users/gregory/Projects/PEG/Grammar.g:1:104: OPT
                {
                mOPT(); 

                }
                break;
            case 19 :
                // /Users/gregory/Projects/PEG/Grammar.g:1:108: ONEMORE
                {
                mONEMORE(); 

                }
                break;
            case 20 :
                // /Users/gregory/Projects/PEG/Grammar.g:1:116: ZEROMORE
                {
                mZEROMORE(); 

                }
                break;

        }

    }


    protected DFA10 dfa10 = new DFA10(this);
    protected DFA13 dfa13 = new DFA13(this);
    static final String DFA10_eotS =
        "\14\uffff";
    static final String DFA10_eofS =
        "\14\uffff";
    static final String DFA10_minS =
        "\1\0\13\uffff";
    static final String DFA10_maxS =
        "\1\uffff\13\uffff";
    static final String DFA10_acceptS =
        "\1\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13";
    static final String DFA10_specialS =
        "\1\0\13\uffff}>";
    static final String[] DFA10_transitionS = {
            "\42\13\1\6\4\13\1\7\26\13\1\11\35\13\1\10\5\13\1\4\3\13\1\5"+
            "\7\13\1\1\3\13\1\2\1\13\1\3\1\12\uff8a\13",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA10_eot = DFA.unpackEncodedString(DFA10_eotS);
    static final short[] DFA10_eof = DFA.unpackEncodedString(DFA10_eofS);
    static final char[] DFA10_min = DFA.unpackEncodedStringToUnsignedChars(DFA10_minS);
    static final char[] DFA10_max = DFA.unpackEncodedStringToUnsignedChars(DFA10_maxS);
    static final short[] DFA10_accept = DFA.unpackEncodedString(DFA10_acceptS);
    static final short[] DFA10_special = DFA.unpackEncodedString(DFA10_specialS);
    static final short[][] DFA10_transition;

    static {
        int numStates = DFA10_transitionS.length;
        DFA10_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA10_transition[i] = DFA.unpackEncodedString(DFA10_transitionS[i]);
        }
    }

    class DFA10 extends DFA {

        public DFA10(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 10;
            this.eot = DFA10_eot;
            this.eof = DFA10_eof;
            this.min = DFA10_min;
            this.max = DFA10_max;
            this.accept = DFA10_accept;
            this.special = DFA10_special;
            this.transition = DFA10_transition;
        }
        public String getDescription() {
            return "53:3: ( 'n' | 'r' | 't' | 'b' | 'f' | '\"' | '\\'' | '\\\\' | '>' | 'u' XDIGIT XDIGIT XDIGIT XDIGIT | . )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            IntStream input = _input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA10_0 = input.LA(1);

                        s = -1;
                        if ( (LA10_0=='n') ) {s = 1;}

                        else if ( (LA10_0=='r') ) {s = 2;}

                        else if ( (LA10_0=='t') ) {s = 3;}

                        else if ( (LA10_0=='b') ) {s = 4;}

                        else if ( (LA10_0=='f') ) {s = 5;}

                        else if ( (LA10_0=='\"') ) {s = 6;}

                        else if ( (LA10_0=='\'') ) {s = 7;}

                        else if ( (LA10_0=='\\') ) {s = 8;}

                        else if ( (LA10_0=='>') ) {s = 9;}

                        else if ( (LA10_0=='u') ) {s = 10;}

                        else if ( ((LA10_0>='\u0000' && LA10_0<='!')||(LA10_0>='#' && LA10_0<='&')||(LA10_0>='(' && LA10_0<='=')||(LA10_0>='?' && LA10_0<='[')||(LA10_0>=']' && LA10_0<='a')||(LA10_0>='c' && LA10_0<='e')||(LA10_0>='g' && LA10_0<='m')||(LA10_0>='o' && LA10_0<='q')||LA10_0=='s'||(LA10_0>='v' && LA10_0<='\uFFFF')) ) {s = 11;}

                        if ( s>=0 ) return s;
                        break;
            }
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 10, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA13_eotS =
        "\1\15\2\uffff\1\17\23\uffff\5\17\1\35\1\uffff";
    static final String DFA13_eofS =
        "\36\uffff";
    static final String DFA13_minS =
        "\1\11\2\uffff\1\162\23\uffff\1\151\1\166\1\141\1\164\1\145\1\60"+
        "\1\uffff";
    static final String DFA13_maxS =
        "\1\175\2\uffff\1\162\23\uffff\1\151\1\166\1\141\1\164\1\145\1\172"+
        "\1\uffff";
    static final String DFA13_acceptS =
        "\1\uffff\1\1\1\2\1\uffff\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14"+
        "\1\15\2\11\1\16\1\17\1\20\1\21\1\22\1\23\1\24\6\uffff\1\3";
    static final String DFA13_specialS =
        "\36\uffff}>";
    static final String[] DFA13_transitionS = {
            "\2\13\2\uffff\1\13\22\uffff\1\13\1\23\1\14\3\uffff\1\22\1\14"+
            "\1\7\1\10\1\26\1\25\3\uffff\1\12\12\20\1\1\1\2\1\uffff\1\6\1"+
            "\uffff\1\24\1\uffff\6\16\24\17\4\uffff\1\17\1\uffff\6\11\11"+
            "\17\1\3\12\17\1\4\1\21\1\5",
            "",
            "",
            "\1\27",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\30",
            "\1\31",
            "\1\32",
            "\1\33",
            "\1\34",
            "\12\17\7\uffff\32\17\4\uffff\1\17\1\uffff\32\17",
            ""
    };

    static final short[] DFA13_eot = DFA.unpackEncodedString(DFA13_eotS);
    static final short[] DFA13_eof = DFA.unpackEncodedString(DFA13_eofS);
    static final char[] DFA13_min = DFA.unpackEncodedStringToUnsignedChars(DFA13_minS);
    static final char[] DFA13_max = DFA.unpackEncodedStringToUnsignedChars(DFA13_maxS);
    static final short[] DFA13_accept = DFA.unpackEncodedString(DFA13_acceptS);
    static final short[] DFA13_special = DFA.unpackEncodedString(DFA13_specialS);
    static final short[][] DFA13_transition;

    static {
        int numStates = DFA13_transitionS.length;
        DFA13_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA13_transition[i] = DFA.unpackEncodedString(DFA13_transitionS[i]);
        }
    }

    class DFA13 extends DFA {

        public DFA13(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 13;
            this.eot = DFA13_eot;
            this.eof = DFA13_eof;
            this.min = DFA13_min;
            this.max = DFA13_max;
            this.accept = DFA13_accept;
            this.special = DFA13_special;
            this.transition = DFA13_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | ID | COMMENT | WS | STRING | XDIGIT | NUMBER | OR | AND | NOT | OPT | ONEMORE | ZEROMORE );";
        }
    }
 

}