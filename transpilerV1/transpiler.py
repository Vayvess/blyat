class Transpiler:
    def __init__(self):
        self.codegen = []
        self.indent_level = 0
    
    def indent(self):
        tabs = "\t" * self.indent_level
        self.codegen.append(tabs)
    
    def visit_receptor(self):
        pass

    def visit_molder(self):
        pass
    
    def visit_behavior(self):
        pass

    def visit_cast(self):
        pass

    def visit_branch(self, node):
        cond = node.condition.accept(self)
    
    def visit_unex(self):
        pass

    def visit_binex(self, node):
        left = node.left.accept(self)
        right = node.right.accept(self)

        return f"({left} {node.op} {right})"
    
    def transpile(self, root):
        
        for node in root.nodes:
            node.accept(self)
        
        return "".join(self.codegen)