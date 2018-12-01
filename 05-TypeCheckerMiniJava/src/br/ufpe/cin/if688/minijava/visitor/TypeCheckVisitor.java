package br.ufpe.cin.if688.minijava.visitor;

import br.ufpe.cin.if688.minijava.ast.And;
import br.ufpe.cin.if688.minijava.ast.ArrayAssign;
import br.ufpe.cin.if688.minijava.ast.ArrayLength;
import br.ufpe.cin.if688.minijava.ast.ArrayLookup;
import br.ufpe.cin.if688.minijava.ast.Assign;
import br.ufpe.cin.if688.minijava.ast.Block;
import br.ufpe.cin.if688.minijava.ast.BooleanType;
import br.ufpe.cin.if688.minijava.ast.Call;
import br.ufpe.cin.if688.minijava.ast.ClassDeclExtends;
import br.ufpe.cin.if688.minijava.ast.ClassDeclSimple;
import br.ufpe.cin.if688.minijava.ast.False;
import br.ufpe.cin.if688.minijava.ast.Formal;
import br.ufpe.cin.if688.minijava.ast.Identifier;
import br.ufpe.cin.if688.minijava.ast.IdentifierExp;
import br.ufpe.cin.if688.minijava.ast.IdentifierType;
import br.ufpe.cin.if688.minijava.ast.If;
import br.ufpe.cin.if688.minijava.ast.IntArrayType;
import br.ufpe.cin.if688.minijava.ast.IntegerLiteral;
import br.ufpe.cin.if688.minijava.ast.IntegerType;
import br.ufpe.cin.if688.minijava.ast.LessThan;
import br.ufpe.cin.if688.minijava.ast.MainClass;
import br.ufpe.cin.if688.minijava.ast.MethodDecl;
import br.ufpe.cin.if688.minijava.ast.Minus;
import br.ufpe.cin.if688.minijava.ast.NewArray;
import br.ufpe.cin.if688.minijava.ast.NewObject;
import br.ufpe.cin.if688.minijava.ast.Not;
import br.ufpe.cin.if688.minijava.ast.Plus;
import br.ufpe.cin.if688.minijava.ast.Print;
import br.ufpe.cin.if688.minijava.ast.Program;
import br.ufpe.cin.if688.minijava.ast.This;
import br.ufpe.cin.if688.minijava.ast.Times;
import br.ufpe.cin.if688.minijava.ast.True;
import br.ufpe.cin.if688.minijava.ast.Type;
import br.ufpe.cin.if688.minijava.ast.VarDecl;
import br.ufpe.cin.if688.minijava.ast.While;
import br.ufpe.cin.if688.minijava.symboltable.Class;
import br.ufpe.cin.if688.minijava.symboltable.Method;
import br.ufpe.cin.if688.minijava.symboltable.SymbolTable;

public class TypeCheckVisitor implements IVisitor<Type> {

	private SymbolTable symbolTable;
	private Class currClass;
	private Method currMethod;

	public TypeCheckVisitor(SymbolTable st) {
		symbolTable = st;
	}

	// MainClass m;
	// ClassDeclList cl;
	public Type visit(Program n) {
		n.m.accept(this);
		for (int i = 0; i < n.cl.size(); i++) {
			n.cl.elementAt(i).accept(this);
		}
		return null;
	}

	// Identifier i1,i2;
	// Statement s;
	public Type visit(MainClass n) {
		currClass = symbolTable.getClass(n.i1.s);
		currMethod = symbolTable.getMethod("main", currClass.getId());


		n.i1.accept(this);
		n.i2.accept(this);
		n.s.accept(this);
		return null;
	}

	// Identifier i;
	// VarDeclList vl;
	// MethodDeclList ml;
	public Type visit(ClassDeclSimple n) {
		currClass = symbolTable.getClass(n.i.s);

		n.i.accept(this);
		for (int i = 0; i < n.vl.size(); i++) {
			n.vl.elementAt(i).accept(this);
		}
		for (int i = 0; i < n.ml.size(); i++) {
			n.ml.elementAt(i).accept(this);
		}
		return null;
	}

	// Identifier i;
	// Identifier j;
	// VarDeclList vl;
	// MethodDeclList ml;
	public Type visit(ClassDeclExtends n) {
		currClass = symbolTable.getClass(n.i.s);


		n.i.accept(this);
		n.j.accept(this);
		for (int i = 0; i < n.vl.size(); i++) {
			n.vl.elementAt(i).accept(this);
		}
		for (int i = 0; i < n.ml.size(); i++) {
			n.ml.elementAt(i).accept(this);
		}
		return null;
	}

	// Type t;
	// Identifier i;
	public Type visit(VarDecl n) {
		n.i.accept(this);
		return n.t.accept(this);
	}

	// Type t;
	// Identifier i;
	// FormalList fl;
	// VarDeclList vl;
	// StatementList sl;
	// Exp e;
	public Type visit(MethodDecl n) {
		currMethod = symbolTable.getMethod(n.i.s, currClass.getId());


		Type type = n.t.accept(this);
		n.i.accept(this);
		for (int i = 0; i < n.fl.size(); i++) {
			n.fl.elementAt(i).accept(this);
		}
		for (int i = 0; i < n.vl.size(); i++) {
			n.vl.elementAt(i).accept(this);
		}
		for (int i = 0; i < n.sl.size(); i++) {
			n.sl.elementAt(i).accept(this);
		}
		Type eType = n.e.accept(this);

		if(!this.symbolTable.compareTypes(eType, type)) {
			System.out.println("Tipo da expressão e tipo declarado são diferentes");
		}

		return type;
	}

	// Type t;
	// Identifier i;
	public Type visit(Formal n) {
		n.t.accept(this);
		n.i.accept(this);
		return null;
	}

	public Type visit(IntArrayType n) {
		return n;
	}

	public Type visit(BooleanType n) {
		return n;
	}

	public Type visit(IntegerType n) {
		return n;
	}

	// String s;
	public Type visit(IdentifierType n) {
		return n;
	}

	// StatementList sl;
	public Type visit(Block n) {
		for (int i = 0; i < n.sl.size(); i++) {
			n.sl.elementAt(i).accept(this);
		}
		return null;
	}

	// Exp e;
	// Statement s1,s2;
	public Type visit(If n) {
		Type eType = n.e.accept(this);

		if(!this.symbolTable.compareTypes(eType, new BooleanType())) {
			System.out.println("Condição não é um booleano");
		}

		n.s1.accept(this);
		n.s2.accept(this);

		return null;
	}

	// Exp e;
	// Statement s;
	public Type visit(While n) {
		Type eType = n.e.accept(this);

		if(!this.symbolTable.compareTypes(eType, new BooleanType())) {
			System.out.println("Condição não é um booleano");
		}

		n.s.accept(this);

		return null;
	}

	// Exp e;
	public Type visit(Print n) {
		n.e.accept(this);
		return null;
	}

	// Identifier i;
	// Exp e;
	public Type visit(Assign n) {
		Type eType = n.e.accept(this);
		Type decType = symbolTable.getVarType(currMethod,currClass,n.i.s);

		if(!this.symbolTable.compareTypes(eType, decType)) {
			System.out.println("Atribuição com variaveis de tipos diferentes");
		}

		n.i.accept(this);

		return null;
	}

	// Identifier i;
	// Exp e1,e2;
	public Type visit(ArrayAssign n) {
		Type varType = n.i.accept(this);
		Type decVarType = symbolTable.getVarType(currMethod,currClass,n.i.s);

		if(!this.symbolTable.compareTypes(varType, decVarType)) {
			System.out.println("Atribuição com variaveis de tipos diferentes");
		}

		n.e1.accept(this);
		n.e2.accept(this);
		return null;
	}

	// Exp e1,e2;
	public Type visit(And n) {
		Type e1Type = n.e1.accept(this);
		Type e2Type = n.e2.accept(this);

		if(!this.symbolTable.compareTypes(e1Type, new BooleanType())){
			if (!this.symbolTable.compareTypes(e2Type, new BooleanType())){
				System.out.println("As duas expressões da operação AND não são booleanos");
				} else {
				System.out.println("Expressão esquerda da operação AND não é um booleano");
				}
			} else if(!this.symbolTable.compareTypes(e2Type, new BooleanType())){
				System.out.println("Expressão direita da operação AND não é um booleano");
			}

		return new BooleanType();
	}

	// Exp e1,e2;
	public Type visit(LessThan n) {
		Type e1Type = n.e1.accept(this);
		Type e2Type = n.e2.accept(this);

		if(!this.symbolTable.compareTypes(e1Type, new IntegerType())){
			if (!this.symbolTable.compareTypes(e2Type, new IntegerType())){
				System.out.println("As duas expressões da operação < não são inteiros");
			} else {
				System.out.println("Expressão esquerda da operação < não é um inteiro");
			}
		} else if(!this.symbolTable.compareTypes(e2Type, new BooleanType())){
			System.out.println("Expressão direita da operação < não é um inteiro");
		}

		return new BooleanType();
	}

	// Exp e1,e2;
	public Type visit(Plus n) {
		Type e1Type = n.e1.accept(this);
		Type e2Type = n.e2.accept(this);

		if(!this.symbolTable.compareTypes(e1Type, new IntegerType())){
			if (!this.symbolTable.compareTypes(e2Type, new IntegerType())){
				System.out.println("As duas expressões da operação + não são inteiros");
			} else {
				System.out.println("Expressão esquerda da operação + não é um inteiro");
			}
		} else if(!this.symbolTable.compareTypes(e2Type, new BooleanType())){
			System.out.println("Expressão direita da operação + não é um inteiro");
		}

		return new IntegerType();
	}

	// Exp e1,e2;
	public Type visit(Minus n) {
		Type e1Type = n.e1.accept(this);
		Type e2Type = n.e2.accept(this);

		if(!this.symbolTable.compareTypes(e1Type, new IntegerType())){
			if (!this.symbolTable.compareTypes(e2Type, new IntegerType())){
				System.out.println("As duas expressões da operação - não são inteiros");
			} else {
				System.out.println("Expressão esquerda da operação - não é um inteiro");
			}
		} else if(!this.symbolTable.compareTypes(e2Type, new BooleanType())){
			System.out.println("Expressão direita da operação - não é um inteiro");
		}

		return new IntegerType();
	}

	// Exp e1,e2;
	public Type visit(Times n) {
		Type e1Type = n.e1.accept(this);
		Type e2Type = n.e2.accept(this);

		if(!this.symbolTable.compareTypes(e1Type, new IntegerType())){
			if (!this.symbolTable.compareTypes(e2Type, new IntegerType())){
				System.out.println("As duas expressões da operação * não são inteiros");
			} else {
				System.out.println("Expressão esquerda da operação * não é um inteiro");
			}
		} else if(!this.symbolTable.compareTypes(e2Type, new BooleanType())){
			System.out.println("Expressão direita da operação * não é um inteiro");
		}

		return new IntegerType();
	}

	// Exp e1,e2;
	public Type visit(ArrayLookup n) {
		Type e1Type = n.e1.accept(this);
		Type e2Type = n.e2.accept(this);

		if(!this.symbolTable.compareTypes(e1Type, new IntArrayType())){
				System.out.println("A expressão não é um array de inteiros");
		}

		if(!this.symbolTable.compareTypes(e1Type, new IntegerType())){
			System.out.println("O índice do array não é um inteiro");
		}

		return new IntegerType();
	}

	// Exp e;
	public Type visit(ArrayLength n) {
		Type eType = n.e.accept(this);

		if(!this.symbolTable.compareTypes(eType, new IntArrayType())){
			System.out.println("A expressão não é um array de inteiros");
		}

		return new IntegerType();
	}

	// Exp e;
	// Identifier i;
	// ExpList el;
	public Type visit(Call n) {
		n.e.accept(this);
		n.i.accept(this);
		for (int i = 0; i < n.el.size(); i++) {
			n.el.elementAt(i).accept(this);
		}
		return null;
	}

	// int i;
	public Type visit(IntegerLiteral n) {
		return new IntegerType();
	}

	public Type visit(True n) {
		return new BooleanType();
	}

	public Type visit(False n) {
		return new BooleanType();
	}

	// String s;
	public Type visit(IdentifierExp n) {
		return null;
	}

	public Type visit(This n) {
		return null;
	}

	// Exp e;
	public Type visit(NewArray n) {
		Type eType = n.e.accept(this);

		if(!this.symbolTable.compareTypes(eType, new IntegerType())){
			System.out.println("O índice do array não é um inteiro");
		}

		return new IntArrayType();
	}

	// Identifier i;
	public Type visit(NewObject n) {
		return n.i.accept(this);
	}

	// Exp e;
	public Type visit(Not n) {
		n.e.accept(this);
		return null;
	}

	// String s;
	public Type visit(Identifier n) {
		return null;
	}
}
