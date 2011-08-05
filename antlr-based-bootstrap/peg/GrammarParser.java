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
import org.antlr.runtime.tree.*;

public class GrammarParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE", "ATTR", "SEQ", "CHOICE", "ATTRS", "MODIFIER", "ID", "COMMENT", "WS", "ESC", "STRING", "XDIGIT", "DIGIT", "NUMBER", "OR", "AND", "NOT", "OPT", "ONEMORE", "ZEROMORE", "'::='", "';'", "'private'", "'{'", "'}'", "'='", "'('", "')'"
    };
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


        public GrammarParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public GrammarParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        
    protected TreeAdaptor adaptor = new CommonTreeAdaptor();

    public void setTreeAdaptor(TreeAdaptor adaptor) {
        this.adaptor = adaptor;
    }
    public TreeAdaptor getTreeAdaptor() {
        return adaptor;
    }

    public String[] getTokenNames() { return GrammarParser.tokenNames; }
    public String getGrammarFileName() { return "/Users/gregory/Projects/PEG/Grammar.g"; }


    //    List<CommonTree> myRules = new ArrayList<CommonTree>();


    public static class literal_return extends ParserRuleReturnScope {
        org.antlr.runtime.tree.CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "literal"
    // /Users/gregory/Projects/PEG/Grammar.g:81:1: literal : ( NUMBER | STRING );
    public final GrammarParser.literal_return literal() throws RecognitionException {
        GrammarParser.literal_return retval = new GrammarParser.literal_return();
        retval.start = input.LT(1);

        org.antlr.runtime.tree.CommonTree root_0 = null;

        Token set1=null;

        org.antlr.runtime.tree.CommonTree set1_tree=null;

        try {
            // /Users/gregory/Projects/PEG/Grammar.g:81:9: ( NUMBER | STRING )
            // /Users/gregory/Projects/PEG/Grammar.g:
            {
            root_0 = (org.antlr.runtime.tree.CommonTree)adaptor.nil();

            set1=(Token)input.LT(1);
            if ( input.LA(1)==STRING||input.LA(1)==NUMBER ) {
                input.consume();
                adaptor.addChild(root_0, (org.antlr.runtime.tree.CommonTree)adaptor.create(set1));
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (org.antlr.runtime.tree.CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (org.antlr.runtime.tree.CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "literal"

    public static class file_return extends ParserRuleReturnScope {
        org.antlr.runtime.tree.CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "file"
    // /Users/gregory/Projects/PEG/Grammar.g:83:1: file : ( attrs | rule )+ ;
    public final GrammarParser.file_return file() throws RecognitionException {
        GrammarParser.file_return retval = new GrammarParser.file_return();
        retval.start = input.LT(1);

        org.antlr.runtime.tree.CommonTree root_0 = null;

        GrammarParser.attrs_return attrs2 = null;

        GrammarParser.rule_return rule3 = null;



        try {
            // /Users/gregory/Projects/PEG/Grammar.g:83:6: ( ( attrs | rule )+ )
            // /Users/gregory/Projects/PEG/Grammar.g:83:8: ( attrs | rule )+
            {
            root_0 = (org.antlr.runtime.tree.CommonTree)adaptor.nil();

            // /Users/gregory/Projects/PEG/Grammar.g:83:8: ( attrs | rule )+
            int cnt1=0;
            loop1:
            do {
                int alt1=3;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==27) ) {
                    alt1=1;
                }
                else if ( (LA1_0==ID||LA1_0==26) ) {
                    alt1=2;
                }


                switch (alt1) {
            	case 1 :
            	    // /Users/gregory/Projects/PEG/Grammar.g:83:9: attrs
            	    {
            	    pushFollow(FOLLOW_attrs_in_file520);
            	    attrs2=attrs();

            	    state._fsp--;

            	    adaptor.addChild(root_0, attrs2.getTree());

            	    }
            	    break;
            	case 2 :
            	    // /Users/gregory/Projects/PEG/Grammar.g:83:17: rule
            	    {
            	    pushFollow(FOLLOW_rule_in_file524);
            	    rule3=rule();

            	    state._fsp--;

            	    adaptor.addChild(root_0, rule3.getTree());

            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);


            }

            retval.stop = input.LT(-1);

            retval.tree = (org.antlr.runtime.tree.CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (org.antlr.runtime.tree.CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "file"

    public static class rule_return extends ParserRuleReturnScope {
        org.antlr.runtime.tree.CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "rule"
    // /Users/gregory/Projects/PEG/Grammar.g:85:1: rule : ( modifier )* ID '::=' choice ( attrs )? ( ';' )? -> ^( RULE ID ( modifier )* choice ( attrs )? ) ;
    public final GrammarParser.rule_return rule() throws RecognitionException {
        GrammarParser.rule_return retval = new GrammarParser.rule_return();
        retval.start = input.LT(1);

        org.antlr.runtime.tree.CommonTree root_0 = null;

        Token ID5=null;
        Token string_literal6=null;
        Token char_literal9=null;
        GrammarParser.modifier_return modifier4 = null;

        GrammarParser.choice_return choice7 = null;

        GrammarParser.attrs_return attrs8 = null;


        org.antlr.runtime.tree.CommonTree ID5_tree=null;
        org.antlr.runtime.tree.CommonTree string_literal6_tree=null;
        org.antlr.runtime.tree.CommonTree char_literal9_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_24=new RewriteRuleTokenStream(adaptor,"token 24");
        RewriteRuleTokenStream stream_25=new RewriteRuleTokenStream(adaptor,"token 25");
        RewriteRuleSubtreeStream stream_modifier=new RewriteRuleSubtreeStream(adaptor,"rule modifier");
        RewriteRuleSubtreeStream stream_choice=new RewriteRuleSubtreeStream(adaptor,"rule choice");
        RewriteRuleSubtreeStream stream_attrs=new RewriteRuleSubtreeStream(adaptor,"rule attrs");
        try {
            // /Users/gregory/Projects/PEG/Grammar.g:86:3: ( ( modifier )* ID '::=' choice ( attrs )? ( ';' )? -> ^( RULE ID ( modifier )* choice ( attrs )? ) )
            // /Users/gregory/Projects/PEG/Grammar.g:86:5: ( modifier )* ID '::=' choice ( attrs )? ( ';' )?
            {
            // /Users/gregory/Projects/PEG/Grammar.g:86:5: ( modifier )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==26) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // /Users/gregory/Projects/PEG/Grammar.g:86:5: modifier
            	    {
            	    pushFollow(FOLLOW_modifier_in_rule537);
            	    modifier4=modifier();

            	    state._fsp--;

            	    stream_modifier.add(modifier4.getTree());

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);

            ID5=(Token)match(input,ID,FOLLOW_ID_in_rule540);  
            stream_ID.add(ID5);

            string_literal6=(Token)match(input,24,FOLLOW_24_in_rule542);  
            stream_24.add(string_literal6);

            pushFollow(FOLLOW_choice_in_rule544);
            choice7=choice();

            state._fsp--;

            stream_choice.add(choice7.getTree());
            // /Users/gregory/Projects/PEG/Grammar.g:86:31: ( attrs )?
            int alt3=2;
            alt3 = dfa3.predict(input);
            switch (alt3) {
                case 1 :
                    // /Users/gregory/Projects/PEG/Grammar.g:86:31: attrs
                    {
                    pushFollow(FOLLOW_attrs_in_rule546);
                    attrs8=attrs();

                    state._fsp--;

                    stream_attrs.add(attrs8.getTree());

                    }
                    break;

            }

            // /Users/gregory/Projects/PEG/Grammar.g:86:38: ( ';' )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==25) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // /Users/gregory/Projects/PEG/Grammar.g:86:38: ';'
                    {
                    char_literal9=(Token)match(input,25,FOLLOW_25_in_rule549);  
                    stream_25.add(char_literal9);


                    }
                    break;

            }



            // AST REWRITE
            // elements: ID, modifier, attrs, choice
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (org.antlr.runtime.tree.CommonTree)adaptor.nil();
            // 86:43: -> ^( RULE ID ( modifier )* choice ( attrs )? )
            {
                // /Users/gregory/Projects/PEG/Grammar.g:86:46: ^( RULE ID ( modifier )* choice ( attrs )? )
                {
                org.antlr.runtime.tree.CommonTree root_1 = (org.antlr.runtime.tree.CommonTree)adaptor.nil();
                root_1 = (org.antlr.runtime.tree.CommonTree)adaptor.becomeRoot((org.antlr.runtime.tree.CommonTree)adaptor.create(RULE, "RULE"), root_1);

                adaptor.addChild(root_1, stream_ID.nextNode());
                // /Users/gregory/Projects/PEG/Grammar.g:86:56: ( modifier )*
                while ( stream_modifier.hasNext() ) {
                    adaptor.addChild(root_1, stream_modifier.nextTree());

                }
                stream_modifier.reset();
                adaptor.addChild(root_1, stream_choice.nextTree());
                // /Users/gregory/Projects/PEG/Grammar.g:86:73: ( attrs )?
                if ( stream_attrs.hasNext() ) {
                    adaptor.addChild(root_1, stream_attrs.nextTree());

                }
                stream_attrs.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (org.antlr.runtime.tree.CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (org.antlr.runtime.tree.CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "rule"

    public static class modifier_return extends ParserRuleReturnScope {
        org.antlr.runtime.tree.CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "modifier"
    // /Users/gregory/Projects/PEG/Grammar.g:91:1: modifier : 'private' -> ^( MODIFIER[\"private\"] ) ;
    public final GrammarParser.modifier_return modifier() throws RecognitionException {
        GrammarParser.modifier_return retval = new GrammarParser.modifier_return();
        retval.start = input.LT(1);

        org.antlr.runtime.tree.CommonTree root_0 = null;

        Token string_literal10=null;

        org.antlr.runtime.tree.CommonTree string_literal10_tree=null;
        RewriteRuleTokenStream stream_26=new RewriteRuleTokenStream(adaptor,"token 26");

        try {
            // /Users/gregory/Projects/PEG/Grammar.g:91:10: ( 'private' -> ^( MODIFIER[\"private\"] ) )
            // /Users/gregory/Projects/PEG/Grammar.g:91:12: 'private'
            {
            string_literal10=(Token)match(input,26,FOLLOW_26_in_modifier577);  
            stream_26.add(string_literal10);



            // AST REWRITE
            // elements: 
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (org.antlr.runtime.tree.CommonTree)adaptor.nil();
            // 91:22: -> ^( MODIFIER[\"private\"] )
            {
                // /Users/gregory/Projects/PEG/Grammar.g:91:25: ^( MODIFIER[\"private\"] )
                {
                org.antlr.runtime.tree.CommonTree root_1 = (org.antlr.runtime.tree.CommonTree)adaptor.nil();
                root_1 = (org.antlr.runtime.tree.CommonTree)adaptor.becomeRoot((org.antlr.runtime.tree.CommonTree)adaptor.create(MODIFIER, "private"), root_1);

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (org.antlr.runtime.tree.CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (org.antlr.runtime.tree.CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "modifier"

    public static class attrs_return extends ParserRuleReturnScope {
        org.antlr.runtime.tree.CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "attrs"
    // /Users/gregory/Projects/PEG/Grammar.g:94:1: attrs : '{' ( attr )* '}' -> ^( ATTRS ( attr )* ) ;
    public final GrammarParser.attrs_return attrs() throws RecognitionException {
        GrammarParser.attrs_return retval = new GrammarParser.attrs_return();
        retval.start = input.LT(1);

        org.antlr.runtime.tree.CommonTree root_0 = null;

        Token char_literal11=null;
        Token char_literal13=null;
        GrammarParser.attr_return attr12 = null;


        org.antlr.runtime.tree.CommonTree char_literal11_tree=null;
        org.antlr.runtime.tree.CommonTree char_literal13_tree=null;
        RewriteRuleTokenStream stream_27=new RewriteRuleTokenStream(adaptor,"token 27");
        RewriteRuleTokenStream stream_28=new RewriteRuleTokenStream(adaptor,"token 28");
        RewriteRuleSubtreeStream stream_attr=new RewriteRuleSubtreeStream(adaptor,"rule attr");
        try {
            // /Users/gregory/Projects/PEG/Grammar.g:94:8: ( '{' ( attr )* '}' -> ^( ATTRS ( attr )* ) )
            // /Users/gregory/Projects/PEG/Grammar.g:95:9: '{' ( attr )* '}'
            {
            char_literal11=(Token)match(input,27,FOLLOW_27_in_attrs603);  
            stream_27.add(char_literal11);

            // /Users/gregory/Projects/PEG/Grammar.g:95:13: ( attr )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==ID) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // /Users/gregory/Projects/PEG/Grammar.g:95:13: attr
            	    {
            	    pushFollow(FOLLOW_attr_in_attrs605);
            	    attr12=attr();

            	    state._fsp--;

            	    stream_attr.add(attr12.getTree());

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            char_literal13=(Token)match(input,28,FOLLOW_28_in_attrs608);  
            stream_28.add(char_literal13);



            // AST REWRITE
            // elements: attr
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (org.antlr.runtime.tree.CommonTree)adaptor.nil();
            // 95:23: -> ^( ATTRS ( attr )* )
            {
                // /Users/gregory/Projects/PEG/Grammar.g:95:26: ^( ATTRS ( attr )* )
                {
                org.antlr.runtime.tree.CommonTree root_1 = (org.antlr.runtime.tree.CommonTree)adaptor.nil();
                root_1 = (org.antlr.runtime.tree.CommonTree)adaptor.becomeRoot((org.antlr.runtime.tree.CommonTree)adaptor.create(ATTRS, "ATTRS"), root_1);

                // /Users/gregory/Projects/PEG/Grammar.g:95:34: ( attr )*
                while ( stream_attr.hasNext() ) {
                    adaptor.addChild(root_1, stream_attr.nextTree());

                }
                stream_attr.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (org.antlr.runtime.tree.CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (org.antlr.runtime.tree.CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "attrs"

    public static class attr_return extends ParserRuleReturnScope {
        org.antlr.runtime.tree.CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "attr"
    // /Users/gregory/Projects/PEG/Grammar.g:98:1: attr : ID '=' value ( ';' )? -> ^( ATTR ID value ) ;
    public final GrammarParser.attr_return attr() throws RecognitionException {
        GrammarParser.attr_return retval = new GrammarParser.attr_return();
        retval.start = input.LT(1);

        org.antlr.runtime.tree.CommonTree root_0 = null;

        Token ID14=null;
        Token char_literal15=null;
        Token char_literal17=null;
        GrammarParser.value_return value16 = null;


        org.antlr.runtime.tree.CommonTree ID14_tree=null;
        org.antlr.runtime.tree.CommonTree char_literal15_tree=null;
        org.antlr.runtime.tree.CommonTree char_literal17_tree=null;
        RewriteRuleTokenStream stream_ID=new RewriteRuleTokenStream(adaptor,"token ID");
        RewriteRuleTokenStream stream_25=new RewriteRuleTokenStream(adaptor,"token 25");
        RewriteRuleTokenStream stream_29=new RewriteRuleTokenStream(adaptor,"token 29");
        RewriteRuleSubtreeStream stream_value=new RewriteRuleSubtreeStream(adaptor,"rule value");
        try {
            // /Users/gregory/Projects/PEG/Grammar.g:98:7: ( ID '=' value ( ';' )? -> ^( ATTR ID value ) )
            // /Users/gregory/Projects/PEG/Grammar.g:98:9: ID '=' value ( ';' )?
            {
            ID14=(Token)match(input,ID,FOLLOW_ID_in_attr629);  
            stream_ID.add(ID14);

            char_literal15=(Token)match(input,29,FOLLOW_29_in_attr631);  
            stream_29.add(char_literal15);

            pushFollow(FOLLOW_value_in_attr633);
            value16=value();

            state._fsp--;

            stream_value.add(value16.getTree());
            // /Users/gregory/Projects/PEG/Grammar.g:98:22: ( ';' )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==25) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // /Users/gregory/Projects/PEG/Grammar.g:98:22: ';'
                    {
                    char_literal17=(Token)match(input,25,FOLLOW_25_in_attr635);  
                    stream_25.add(char_literal17);


                    }
                    break;

            }



            // AST REWRITE
            // elements: value, ID
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (org.antlr.runtime.tree.CommonTree)adaptor.nil();
            // 98:27: -> ^( ATTR ID value )
            {
                // /Users/gregory/Projects/PEG/Grammar.g:98:30: ^( ATTR ID value )
                {
                org.antlr.runtime.tree.CommonTree root_1 = (org.antlr.runtime.tree.CommonTree)adaptor.nil();
                root_1 = (org.antlr.runtime.tree.CommonTree)adaptor.becomeRoot((org.antlr.runtime.tree.CommonTree)adaptor.create(ATTR, "ATTR"), root_1);

                adaptor.addChild(root_1, stream_ID.nextNode());
                adaptor.addChild(root_1, stream_value.nextTree());

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (org.antlr.runtime.tree.CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (org.antlr.runtime.tree.CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "attr"

    public static class value_return extends ParserRuleReturnScope {
        org.antlr.runtime.tree.CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "value"
    // /Users/gregory/Projects/PEG/Grammar.g:101:1: value : ( literal | ID );
    public final GrammarParser.value_return value() throws RecognitionException {
        GrammarParser.value_return retval = new GrammarParser.value_return();
        retval.start = input.LT(1);

        org.antlr.runtime.tree.CommonTree root_0 = null;

        Token ID19=null;
        GrammarParser.literal_return literal18 = null;


        org.antlr.runtime.tree.CommonTree ID19_tree=null;

        try {
            // /Users/gregory/Projects/PEG/Grammar.g:101:7: ( literal | ID )
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==STRING||LA7_0==NUMBER) ) {
                alt7=1;
            }
            else if ( (LA7_0==ID) ) {
                alt7=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }
            switch (alt7) {
                case 1 :
                    // /Users/gregory/Projects/PEG/Grammar.g:101:9: literal
                    {
                    root_0 = (org.antlr.runtime.tree.CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_literal_in_value656);
                    literal18=literal();

                    state._fsp--;

                    adaptor.addChild(root_0, literal18.getTree());

                    }
                    break;
                case 2 :
                    // /Users/gregory/Projects/PEG/Grammar.g:101:19: ID
                    {
                    root_0 = (org.antlr.runtime.tree.CommonTree)adaptor.nil();

                    ID19=(Token)match(input,ID,FOLLOW_ID_in_value660); 
                    ID19_tree = (org.antlr.runtime.tree.CommonTree)adaptor.create(ID19);
                    adaptor.addChild(root_0, ID19_tree);


                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (org.antlr.runtime.tree.CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (org.antlr.runtime.tree.CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "value"

    public static class choice_return extends ParserRuleReturnScope {
        org.antlr.runtime.tree.CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "choice"
    // /Users/gregory/Projects/PEG/Grammar.g:105:1: choice : sequence ( OR sequence )* -> ^( CHOICE ( sequence )+ ) ;
    public final GrammarParser.choice_return choice() throws RecognitionException {
        GrammarParser.choice_return retval = new GrammarParser.choice_return();
        retval.start = input.LT(1);

        org.antlr.runtime.tree.CommonTree root_0 = null;

        Token OR21=null;
        GrammarParser.sequence_return sequence20 = null;

        GrammarParser.sequence_return sequence22 = null;


        org.antlr.runtime.tree.CommonTree OR21_tree=null;
        RewriteRuleTokenStream stream_OR=new RewriteRuleTokenStream(adaptor,"token OR");
        RewriteRuleSubtreeStream stream_sequence=new RewriteRuleSubtreeStream(adaptor,"rule sequence");
        try {
            // /Users/gregory/Projects/PEG/Grammar.g:106:2: ( sequence ( OR sequence )* -> ^( CHOICE ( sequence )+ ) )
            // /Users/gregory/Projects/PEG/Grammar.g:106:4: sequence ( OR sequence )*
            {
            pushFollow(FOLLOW_sequence_in_choice673);
            sequence20=sequence();

            state._fsp--;

            stream_sequence.add(sequence20.getTree());
            // /Users/gregory/Projects/PEG/Grammar.g:106:13: ( OR sequence )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==OR) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // /Users/gregory/Projects/PEG/Grammar.g:106:14: OR sequence
            	    {
            	    OR21=(Token)match(input,OR,FOLLOW_OR_in_choice676);  
            	    stream_OR.add(OR21);

            	    pushFollow(FOLLOW_sequence_in_choice678);
            	    sequence22=sequence();

            	    state._fsp--;

            	    stream_sequence.add(sequence22.getTree());

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);



            // AST REWRITE
            // elements: sequence
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (org.antlr.runtime.tree.CommonTree)adaptor.nil();
            // 106:28: -> ^( CHOICE ( sequence )+ )
            {
                // /Users/gregory/Projects/PEG/Grammar.g:106:31: ^( CHOICE ( sequence )+ )
                {
                org.antlr.runtime.tree.CommonTree root_1 = (org.antlr.runtime.tree.CommonTree)adaptor.nil();
                root_1 = (org.antlr.runtime.tree.CommonTree)adaptor.becomeRoot((org.antlr.runtime.tree.CommonTree)adaptor.create(CHOICE, "CHOICE"), root_1);

                if ( !(stream_sequence.hasNext()) ) {
                    throw new RewriteEarlyExitException();
                }
                while ( stream_sequence.hasNext() ) {
                    adaptor.addChild(root_1, stream_sequence.nextTree());

                }
                stream_sequence.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (org.antlr.runtime.tree.CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (org.antlr.runtime.tree.CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "choice"

    public static class sequence_return extends ParserRuleReturnScope {
        org.antlr.runtime.tree.CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "sequence"
    // /Users/gregory/Projects/PEG/Grammar.g:108:1: sequence : ( option )* -> ^( SEQ ( option )* ) ;
    public final GrammarParser.sequence_return sequence() throws RecognitionException {
        GrammarParser.sequence_return retval = new GrammarParser.sequence_return();
        retval.start = input.LT(1);

        org.antlr.runtime.tree.CommonTree root_0 = null;

        GrammarParser.option_return option23 = null;


        RewriteRuleSubtreeStream stream_option=new RewriteRuleSubtreeStream(adaptor,"rule option");
        try {
            // /Users/gregory/Projects/PEG/Grammar.g:109:2: ( ( option )* -> ^( SEQ ( option )* ) )
            // /Users/gregory/Projects/PEG/Grammar.g:109:4: ( option )*
            {
            // /Users/gregory/Projects/PEG/Grammar.g:109:4: ( option )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==ID) ) {
                    int LA9_2 = input.LA(2);

                    if ( (LA9_2==EOF||LA9_2==ID||LA9_2==STRING||(LA9_2>=NUMBER && LA9_2<=ZEROMORE)||(LA9_2>=25 && LA9_2<=27)||(LA9_2>=30 && LA9_2<=31)) ) {
                        alt9=1;
                    }


                }
                else if ( (LA9_0==STRING||LA9_0==NUMBER||(LA9_0>=AND && LA9_0<=NOT)||LA9_0==30) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // /Users/gregory/Projects/PEG/Grammar.g:109:4: option
            	    {
            	    pushFollow(FOLLOW_option_in_sequence699);
            	    option23=option();

            	    state._fsp--;

            	    stream_option.add(option23.getTree());

            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);



            // AST REWRITE
            // elements: option
            // token labels: 
            // rule labels: retval
            // token list labels: 
            // rule list labels: 
            // wildcard labels: 
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

            root_0 = (org.antlr.runtime.tree.CommonTree)adaptor.nil();
            // 109:12: -> ^( SEQ ( option )* )
            {
                // /Users/gregory/Projects/PEG/Grammar.g:109:15: ^( SEQ ( option )* )
                {
                org.antlr.runtime.tree.CommonTree root_1 = (org.antlr.runtime.tree.CommonTree)adaptor.nil();
                root_1 = (org.antlr.runtime.tree.CommonTree)adaptor.becomeRoot((org.antlr.runtime.tree.CommonTree)adaptor.create(SEQ, "SEQ"), root_1);

                // /Users/gregory/Projects/PEG/Grammar.g:109:21: ( option )*
                while ( stream_option.hasNext() ) {
                    adaptor.addChild(root_1, stream_option.nextTree());

                }
                stream_option.reset();

                adaptor.addChild(root_0, root_1);
                }

            }

            retval.tree = root_0;
            }

            retval.stop = input.LT(-1);

            retval.tree = (org.antlr.runtime.tree.CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (org.antlr.runtime.tree.CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "sequence"

    public static class option_return extends ParserRuleReturnScope {
        org.antlr.runtime.tree.CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "option"
    // /Users/gregory/Projects/PEG/Grammar.g:111:1: option : ( simple ( quantifier )? | AND simple | NOT simple );
    public final GrammarParser.option_return option() throws RecognitionException {
        GrammarParser.option_return retval = new GrammarParser.option_return();
        retval.start = input.LT(1);

        org.antlr.runtime.tree.CommonTree root_0 = null;

        Token AND26=null;
        Token NOT28=null;
        GrammarParser.simple_return simple24 = null;

        GrammarParser.quantifier_return quantifier25 = null;

        GrammarParser.simple_return simple27 = null;

        GrammarParser.simple_return simple29 = null;


        org.antlr.runtime.tree.CommonTree AND26_tree=null;
        org.antlr.runtime.tree.CommonTree NOT28_tree=null;

        try {
            // /Users/gregory/Projects/PEG/Grammar.g:111:8: ( simple ( quantifier )? | AND simple | NOT simple )
            int alt11=3;
            switch ( input.LA(1) ) {
            case ID:
            case STRING:
            case NUMBER:
            case 30:
                {
                alt11=1;
                }
                break;
            case AND:
                {
                alt11=2;
                }
                break;
            case NOT:
                {
                alt11=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }

            switch (alt11) {
                case 1 :
                    // /Users/gregory/Projects/PEG/Grammar.g:112:2: simple ( quantifier )?
                    {
                    root_0 = (org.antlr.runtime.tree.CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_simple_in_option720);
                    simple24=simple();

                    state._fsp--;

                    adaptor.addChild(root_0, simple24.getTree());
                    // /Users/gregory/Projects/PEG/Grammar.g:112:19: ( quantifier )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( ((LA10_0>=OPT && LA10_0<=ZEROMORE)) ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // /Users/gregory/Projects/PEG/Grammar.g:112:19: quantifier
                            {
                            pushFollow(FOLLOW_quantifier_in_option722);
                            quantifier25=quantifier();

                            state._fsp--;

                            root_0 = (org.antlr.runtime.tree.CommonTree)adaptor.becomeRoot(quantifier25.getTree(), root_0);

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // /Users/gregory/Projects/PEG/Grammar.g:113:4: AND simple
                    {
                    root_0 = (org.antlr.runtime.tree.CommonTree)adaptor.nil();

                    AND26=(Token)match(input,AND,FOLLOW_AND_in_option729); 
                    AND26_tree = (org.antlr.runtime.tree.CommonTree)adaptor.create(AND26);
                    root_0 = (org.antlr.runtime.tree.CommonTree)adaptor.becomeRoot(AND26_tree, root_0);

                    pushFollow(FOLLOW_simple_in_option732);
                    simple27=simple();

                    state._fsp--;

                    adaptor.addChild(root_0, simple27.getTree());

                    }
                    break;
                case 3 :
                    // /Users/gregory/Projects/PEG/Grammar.g:114:4: NOT simple
                    {
                    root_0 = (org.antlr.runtime.tree.CommonTree)adaptor.nil();

                    NOT28=(Token)match(input,NOT,FOLLOW_NOT_in_option737); 
                    NOT28_tree = (org.antlr.runtime.tree.CommonTree)adaptor.create(NOT28);
                    root_0 = (org.antlr.runtime.tree.CommonTree)adaptor.becomeRoot(NOT28_tree, root_0);

                    pushFollow(FOLLOW_simple_in_option740);
                    simple29=simple();

                    state._fsp--;

                    adaptor.addChild(root_0, simple29.getTree());

                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (org.antlr.runtime.tree.CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (org.antlr.runtime.tree.CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "option"

    public static class quantifier_return extends ParserRuleReturnScope {
        org.antlr.runtime.tree.CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "quantifier"
    // /Users/gregory/Projects/PEG/Grammar.g:117:1: quantifier : ( OPT | ONEMORE | ZEROMORE );
    public final GrammarParser.quantifier_return quantifier() throws RecognitionException {
        GrammarParser.quantifier_return retval = new GrammarParser.quantifier_return();
        retval.start = input.LT(1);

        org.antlr.runtime.tree.CommonTree root_0 = null;

        Token set30=null;

        org.antlr.runtime.tree.CommonTree set30_tree=null;

        try {
            // /Users/gregory/Projects/PEG/Grammar.g:118:2: ( OPT | ONEMORE | ZEROMORE )
            // /Users/gregory/Projects/PEG/Grammar.g:
            {
            root_0 = (org.antlr.runtime.tree.CommonTree)adaptor.nil();

            set30=(Token)input.LT(1);
            if ( (input.LA(1)>=OPT && input.LA(1)<=ZEROMORE) ) {
                input.consume();
                adaptor.addChild(root_0, (org.antlr.runtime.tree.CommonTree)adaptor.create(set30));
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

            retval.stop = input.LT(-1);

            retval.tree = (org.antlr.runtime.tree.CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (org.antlr.runtime.tree.CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "quantifier"

    public static class simple_return extends ParserRuleReturnScope {
        org.antlr.runtime.tree.CommonTree tree;
        public Object getTree() { return tree; }
    };

    // $ANTLR start "simple"
    // /Users/gregory/Projects/PEG/Grammar.g:123:1: simple : ( ID | literal | '(' choice ')' -> choice );
    public final GrammarParser.simple_return simple() throws RecognitionException {
        GrammarParser.simple_return retval = new GrammarParser.simple_return();
        retval.start = input.LT(1);

        org.antlr.runtime.tree.CommonTree root_0 = null;

        Token ID31=null;
        Token char_literal33=null;
        Token char_literal35=null;
        GrammarParser.literal_return literal32 = null;

        GrammarParser.choice_return choice34 = null;


        org.antlr.runtime.tree.CommonTree ID31_tree=null;
        org.antlr.runtime.tree.CommonTree char_literal33_tree=null;
        org.antlr.runtime.tree.CommonTree char_literal35_tree=null;
        RewriteRuleTokenStream stream_30=new RewriteRuleTokenStream(adaptor,"token 30");
        RewriteRuleTokenStream stream_31=new RewriteRuleTokenStream(adaptor,"token 31");
        RewriteRuleSubtreeStream stream_choice=new RewriteRuleSubtreeStream(adaptor,"rule choice");
        try {
            // /Users/gregory/Projects/PEG/Grammar.g:123:9: ( ID | literal | '(' choice ')' -> choice )
            int alt12=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt12=1;
                }
                break;
            case STRING:
            case NUMBER:
                {
                alt12=2;
                }
                break;
            case 30:
                {
                alt12=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;
            }

            switch (alt12) {
                case 1 :
                    // /Users/gregory/Projects/PEG/Grammar.g:123:11: ID
                    {
                    root_0 = (org.antlr.runtime.tree.CommonTree)adaptor.nil();

                    ID31=(Token)match(input,ID,FOLLOW_ID_in_simple773); 
                    ID31_tree = (org.antlr.runtime.tree.CommonTree)adaptor.create(ID31);
                    adaptor.addChild(root_0, ID31_tree);


                    }
                    break;
                case 2 :
                    // /Users/gregory/Projects/PEG/Grammar.g:124:4: literal
                    {
                    root_0 = (org.antlr.runtime.tree.CommonTree)adaptor.nil();

                    pushFollow(FOLLOW_literal_in_simple778);
                    literal32=literal();

                    state._fsp--;

                    adaptor.addChild(root_0, literal32.getTree());

                    }
                    break;
                case 3 :
                    // /Users/gregory/Projects/PEG/Grammar.g:125:4: '(' choice ')'
                    {
                    char_literal33=(Token)match(input,30,FOLLOW_30_in_simple783);  
                    stream_30.add(char_literal33);

                    pushFollow(FOLLOW_choice_in_simple785);
                    choice34=choice();

                    state._fsp--;

                    stream_choice.add(choice34.getTree());
                    char_literal35=(Token)match(input,31,FOLLOW_31_in_simple787);  
                    stream_31.add(char_literal35);



                    // AST REWRITE
                    // elements: choice
                    // token labels: 
                    // rule labels: retval
                    // token list labels: 
                    // rule list labels: 
                    // wildcard labels: 
                    retval.tree = root_0;
                    RewriteRuleSubtreeStream stream_retval=new RewriteRuleSubtreeStream(adaptor,"rule retval",retval!=null?retval.tree:null);

                    root_0 = (org.antlr.runtime.tree.CommonTree)adaptor.nil();
                    // 125:19: -> choice
                    {
                        adaptor.addChild(root_0, stream_choice.nextTree());

                    }

                    retval.tree = root_0;
                    }
                    break;

            }
            retval.stop = input.LT(-1);

            retval.tree = (org.antlr.runtime.tree.CommonTree)adaptor.rulePostProcessing(root_0);
            adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
    	retval.tree = (org.antlr.runtime.tree.CommonTree)adaptor.errorNode(input, retval.start, input.LT(-1), re);

        }
        finally {
        }
        return retval;
    }
    // $ANTLR end "simple"

    // Delegated rules


    protected DFA3 dfa3 = new DFA3(this);
    static final String DFA3_eotS =
        "\11\uffff";
    static final String DFA3_eofS =
        "\1\2\10\uffff";
    static final String DFA3_minS =
        "\2\12\1\uffff\1\35\1\uffff\4\12";
    static final String DFA3_maxS =
        "\1\33\1\34\1\uffff\1\35\1\uffff\1\21\3\34";
    static final String DFA3_acceptS =
        "\2\uffff\1\2\1\uffff\1\1\4\uffff";
    static final String DFA3_specialS =
        "\11\uffff}>";
    static final String[] DFA3_transitionS = {
            "\1\2\16\uffff\2\2\1\1",
            "\1\3\21\uffff\1\4",
            "",
            "\1\5",
            "",
            "\1\7\3\uffff\1\6\2\uffff\1\6",
            "\1\3\16\uffff\1\10\2\uffff\1\4",
            "\1\3\16\uffff\1\10\2\uffff\1\4",
            "\1\3\21\uffff\1\4"
    };

    static final short[] DFA3_eot = DFA.unpackEncodedString(DFA3_eotS);
    static final short[] DFA3_eof = DFA.unpackEncodedString(DFA3_eofS);
    static final char[] DFA3_min = DFA.unpackEncodedStringToUnsignedChars(DFA3_minS);
    static final char[] DFA3_max = DFA.unpackEncodedStringToUnsignedChars(DFA3_maxS);
    static final short[] DFA3_accept = DFA.unpackEncodedString(DFA3_acceptS);
    static final short[] DFA3_special = DFA.unpackEncodedString(DFA3_specialS);
    static final short[][] DFA3_transition;

    static {
        int numStates = DFA3_transitionS.length;
        DFA3_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA3_transition[i] = DFA.unpackEncodedString(DFA3_transitionS[i]);
        }
    }

    class DFA3 extends DFA {

        public DFA3(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 3;
            this.eot = DFA3_eot;
            this.eof = DFA3_eof;
            this.min = DFA3_min;
            this.max = DFA3_max;
            this.accept = DFA3_accept;
            this.special = DFA3_special;
            this.transition = DFA3_transition;
        }
        public String getDescription() {
            return "86:31: ( attrs )?";
        }
    }
 

    public static final BitSet FOLLOW_set_in_literal0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attrs_in_file520 = new BitSet(new long[]{0x000000000C000402L});
    public static final BitSet FOLLOW_rule_in_file524 = new BitSet(new long[]{0x000000000C000402L});
    public static final BitSet FOLLOW_modifier_in_rule537 = new BitSet(new long[]{0x0000000004000400L});
    public static final BitSet FOLLOW_ID_in_rule540 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_24_in_rule542 = new BitSet(new long[]{0x00000000401E4400L});
    public static final BitSet FOLLOW_choice_in_rule544 = new BitSet(new long[]{0x000000000A000002L});
    public static final BitSet FOLLOW_attrs_in_rule546 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_25_in_rule549 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_modifier577 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_attrs603 = new BitSet(new long[]{0x0000000010000400L});
    public static final BitSet FOLLOW_attr_in_attrs605 = new BitSet(new long[]{0x0000000010000400L});
    public static final BitSet FOLLOW_28_in_attrs608 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_attr629 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_29_in_attr631 = new BitSet(new long[]{0x0000000000024400L});
    public static final BitSet FOLLOW_value_in_attr633 = new BitSet(new long[]{0x0000000002000002L});
    public static final BitSet FOLLOW_25_in_attr635 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_value656 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_value660 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sequence_in_choice673 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_OR_in_choice676 = new BitSet(new long[]{0x00000000401E4400L});
    public static final BitSet FOLLOW_sequence_in_choice678 = new BitSet(new long[]{0x0000000000040002L});
    public static final BitSet FOLLOW_option_in_sequence699 = new BitSet(new long[]{0x00000000401A4402L});
    public static final BitSet FOLLOW_simple_in_option720 = new BitSet(new long[]{0x0000000000E00002L});
    public static final BitSet FOLLOW_quantifier_in_option722 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AND_in_option729 = new BitSet(new long[]{0x0000000040024400L});
    public static final BitSet FOLLOW_simple_in_option732 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_option737 = new BitSet(new long[]{0x0000000040024400L});
    public static final BitSet FOLLOW_simple_in_option740 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_quantifier0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_simple773 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_literal_in_simple778 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_simple783 = new BitSet(new long[]{0x00000000401E4400L});
    public static final BitSet FOLLOW_choice_in_simple785 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_31_in_simple787 = new BitSet(new long[]{0x0000000000000002L});

}