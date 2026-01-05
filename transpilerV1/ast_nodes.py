class RootNode:
    def __init__(self):
        self.nodes = []
    
    def add(self, node):
        self.nodes.append(node)
    
    def accept(self, visitor):
        return visitor.visit_root(self)

class ReceptorNode:
    def __init__(self, name, structure, molders, behaviors):
        self.name = name
        self.structure = structure
        self.molders = molders
        self.behaviors = behaviors
    
    def accept(self, visitor):
        return visitor.visit_receptor(self)

class MolderNode:
    def __init__(self, name, params, body):
        self.name = name
        self.params = params
        self.body = body
    
    def accept(self, visitor):
        return visitor.visit_molder(self)

class BehaviorNode:
    def __init__(self, name, params, body):
        self.name = name
        self.params = params
        self.body = body
    
    def accept(self, visitor):
        return visitor.visit_behavior(self)

class BranchNode:
    def __init__(self, branches):
        self.branches = branches
    
    def accept(self, visitor):
        return visitor.visit_branch(self)

class UnaryExprNode:
    def __init__(self, op, operand, is_infix):
        self.op = op
        self.operand = operand
        self.is_infix = is_infix
    
    def accept(self, visitor):
        return visitor.visit_unex(self)

class BinExprNode:
    def __init__(self, left, op, right):
        self.left = left
        self.op = op
        self.right = right
    
    def accept(self, visitor):
        return visitor.visit_binex(self)

class IndexNode:
    def __init__(self, left, index):
        self.left = left
        self.index = index
    
    def accept(self, visitor):
        return visitor.visit_indexed(self)

class FieldAccessNode:
    def __init__(self, left, name):
        self.left = left
        self.name = name
    
    def accept(self, visitor):
        return visitor.visit_field_access(self)

class FunCallNode:
    def __init__(self, left, args):
        self.left = left
        self.args = args
    
    def accept(self, visitor):
        return visitor.visit_fcall(self)

class ReturnNode:
    def __init__(self, expr):
        self.expr = expr
    
    def accept(self, visitor):
        visitor.visit_return(self)

class WhileNode:
    def __init__(self, condition, body):
        self.condition = condition
        self.body = body
    
    def accept(self, visitor):
        visitor.visit_while(self)

class CastNode:
    def __init__(self, dest, msg):
        self.dest = dest
        self.msg = msg
    
    def accept(self, visitor):
        return visitor.visit_cast(self)

class ExprStmtNode:
    def __init__(self, expr):
        self.expr = expr
    
    def accept(self, visitor):
        visitor.visit_exprstmt(self)

class AssignmentNode:
    def __init__(self, op, left, right):
        self.op = op
        self.left = left
        self.right = right
    
    def accept(self, visitor):
        visitor.visit_assignement(self)

class VarDeclNode:
    def __init__(self, vtype, name, init):
        self.type = vtype
        self.name = name
        self.init = init
    
    def accept(self, visitor):
        visitor.visit_vardecl(self)

class IdentifierNode:
    def __init__(self, token):
        self.token = token
        self.name = token.value

    def accept(self, visitor):
        return visitor.visit_identifier(self)

# === LITERALS ===
class IntLiteralNode:
    def __init__(self, token):
        self.token = token
        self.value = token.value

    def accept(self, visitor):
        return visitor.visit_int_literal(self)

class FloatLiteralNode:
    def __init__(self, token):
        self.token = token
        self.value = token.value

    def accept(self, visitor):
        return visitor.visit_float_literal(self)

class StringLiteralNode:
    def __init__(self, token):
        self.token = token
        self.value = token.value

    def accept(self, visitor):
        return visitor.visit_string_literal(self)

class BoolLiteralNode:
    def __init__(self, token):
        self.token = token
        self.value = token.value

    def accept(self, visitor):
        return visitor.visit_bool_literal(self)

class MessageNode:
    def __init__(self, label, args):
        self.label = label
        self.args = args

    def accept(self, visitor):
        return visitor.visit_message(self)
