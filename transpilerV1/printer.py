class Printer:
    def __init__(self):
        self.codegen = []
        self.ident_level = 0

    def ident(self):
        tabs = "\t" * self.indent_level
        self.codegen.append(tabs)
    
    def visit_receptor(self, node):
        self.codegen.append(f"[receptor {node.name.value}]\n")

        self.codegen.append(f"[]")
        for field_type, field_name in node.structure:
            self.codegen.append(
                f"[field: {field_type.value}, {field_name.value}]"
            )
    
    def visit_molder(self):
        pass

    def visit_behavior(self):
        pass

    def visit_cast(self):
        pass

    def visit_branch(self):
        pass

    def print(self, root):
        self.codegen = []
        self.ident_level = 0

        for node in root.nodes:
            node.accept(self)
        
        print("".join(self.codegen))
