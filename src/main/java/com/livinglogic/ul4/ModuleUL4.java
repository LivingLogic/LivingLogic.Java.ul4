/*
** Copyright 2021-2024 by LivingLogic AG, Bayreuth/Germany
** All Rights Reserved
** See LICENSE for the license
*/

package com.livinglogic.ul4;

import java.util.Set;

import com.livinglogic.ul4on.Utils;
import com.livinglogic.ul4on.Encoder;
import com.livinglogic.ul4on.Decoder;


public class ModuleUL4 extends Module
{
	public ModuleUL4()
	{
		super("ul4", "UL4 - A templating language");
		addObject("__version__", Template.getVersion());
		addObject(AST.type);
		addObject(TextAST.type);
		addObject(IndentAST.type);
		addObject(LineEndAST.type);
		addObject(CodeAST.type);
		addObject(ConstAST.type);
		addObject(SeqItemAST.type);
		addObject(UnpackSeqItemAST.type);
		addObject(ListAST.type);
		addObject(ListComprehensionAST.type);
		addObject(SetAST.type);
		addObject(SetComprehensionAST.type);
		addObject(DictItemAST.type);
		addObject(UnpackDictItemAST.type);
		addObject(DictAST.type);
		addObject(DictComprehensionAST.type);
		addObject(GeneratorExpressionAST.type);
		addObject(VarAST.type);
		addObject(BlockAST.type);
		addObject(ConditionalBlocksAST.type);
		addObject(IfBlockAST.type);
		addObject(ElIfBlockAST.type);
		addObject(ElseBlockAST.type);
		addObject(ForBlockAST.type);
		addObject(WhileBlockAST.type);
		addObject(BreakAST.type);
		addObject(ContinueAST.type);
		addObject(AttrAST.type);
		addObject(SliceAST.type);
		addObject(UnaryAST.type);
		addObject(NotAST.type);
		addObject(IfAST.type);
		addObject(NegAST.type);
		addObject(BitNotAST.type);
		addObject(PrintAST.type);
		addObject(PrintXAST.type);
		addObject(ReturnAST.type);
		addObject(BinaryAST.type);
		addObject(ItemAST.type);
		addObject(ShiftLeftAST.type);
		addObject(ShiftRightAST.type);
		addObject(BitAndAST.type);
		addObject(BitXOrAST.type);
		addObject(BitOrAST.type);
		addObject(IsAST.type);
		addObject(IsNotAST.type);
		addObject(EQAST.type);
		addObject(NEAST.type);
		addObject(LTAST.type);
		addObject(LEAST.type);
		addObject(GTAST.type);
		addObject(GEAST.type);
		addObject(ContainsAST.type);
		addObject(NotContainsAST.type);
		addObject(AddAST.type);
		addObject(SubAST.type);
		addObject(MulAST.type);
		addObject(FloorDivAST.type);
		addObject(TrueDivAST.type);
		addObject(OrAST.type);
		addObject(AndAST.type);
		addObject(ModAST.type);
		addObject(ChangeVarAST.type);
		addObject(SetVarAST.type);
		addObject(AddVarAST.type);
		addObject(SubVarAST.type);
		addObject(MulVarAST.type);
		addObject(FloorDivVarAST.type);
		addObject(TrueDivVarAST.type);
		addObject(ModVarAST.type);
		addObject(ShiftLeftVarAST.type);
		addObject(ShiftRightVarAST.type);
		addObject(BitAndVarAST.type);
		addObject(BitXOrVarAST.type);
		addObject(BitOrVarAST.type);
		addObject(PositionalArgumentAST.type);
		addObject(KeywordArgumentAST.type);
		addObject(UnpackListArgumentAST.type);
		addObject(UnpackDictArgumentAST.type);
		addObject(CallAST.type);
		addObject(RenderAST.type);
		addObject(RenderXAST.type);
		addObject(RenderXOrPrintXAST.type);
		addObject(RenderXOrPrintAST.type);
		addObject(RenderOrPrintXAST.type);
		addObject(RenderOrPrintAST.type);
		addObject(RenderBlockAST.type);
		addObject(RenderBlocksAST.type);
		addObject(SignatureAST.type);
		addObject(Template.type);
		addObject(TemplateClosure.type);
	}

	public static final Module module = new ModuleUL4();
}
