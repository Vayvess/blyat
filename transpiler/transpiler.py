class Transpiler:
    def __init__(self):
        self.codegen = []
        self.indent_level = 0
    
    def indent(self):
        tabs = "\t" * self.indent_level
        self.codegen.append(tabs)
    
    def visit_receptor(self):
        pass

    def visit_structure(self):
        pass

    def visit_molder(self):
        pass
    
    def visit_behavior(self):
        pass

    def visit_cast(self):
        pass

    def visit_branch(self):
        pass
    
    def transpile(self, root):
        
        for node in root.nodes:
            node.accept(self)
        
        return "".join(self.codegen)