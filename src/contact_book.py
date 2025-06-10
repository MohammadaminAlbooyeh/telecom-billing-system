from collections import defaultdict


class ContactBook:
    def __init__(self):
        self.contacts = defaultdict(dict)

    def add_contact(self, name: str, phone: str, email: str = None):
        """
        Adds a new contact to the contact book.
        :param name: Name of the contact
        :param phone: Phone number of the contact
        :param email: Email of the contact (optional)
        :return: True if contact is added successfully, False otherwise.
        """
        if name in self.contacts:
            print("Contact already exists")
            return False
        
        # self.contacts[name] = {}
        self.contacts[name]['phone'] = phone
        self.contacts[name]['email'] = email
        return True             

    def view_contact(self, name: str):
        """
        View a contact from the contact book.
        :param name: Name of the contact to be viewed.
        """
        if name in self.contacts:
            print(f"Name: {name}")
            print(f"Phone: {self.contacts[name]['phone']}")
            print(f"Email: {self.contacts[name]['email']}")
            print('_' * 50)
        else:
            print(f"Contact {name} does not exist")

    def delete_contact(self, name: str):
        """
        Deletes a contact from the contact book.
        :param name: Name of the contact to be deleted.
        """
        if name in self.contacts:
            del self.contacts[name]
            print(f"Contact {name} deleted successfully")
        else:
            print(f"Contact {name} does not exist")

    def update_contact(self, name: str, phone: str=None, email: str=None):
        """
        Updates a contact in the contact book.
        :param name: Name of the contact to be updated.
        :param phone: New phone number of the contact (optional)
        :param email: New email of the contact (optional)
        """
        if name in self.contacts:
            if phone:
                self.contacts[name]['phone'] = phone
            if email:
                self.contacts[name]['email'] = email
            print(f"Contact {name} updated successfully")
            return

        print("Contact not found")    


if __name__ == '__main__':
    book = ContactBook()

while True:
    print("Welcome to the Contact Book!")
    print("1. Add a new contact")
    print("2. Edit for a contact")
    print("3. View a contact")
    print("4. Delete contacts")
    print("5. Exit")

    user_choice = input("Enter your choice: ")

    if user_choice == "5":
        break
    
    elif user_choice == "1":
        name = input("Enter name: ")
        phone = input("Enter phone number: ")
        email = input("Enter email: ")

        book.add_contact(name, phone, email)
    elif user_choice == "2":
        name = input("Enter name: ")
        phone = input("Enter new phone number: ")
        email = input("Enter new email: ")

        book.update_contact(name, phone, email)
    elif user_choice == "3":
        name = input("Enter name: ")

        book.view_contact(name)
    elif user_choice == "4":
        name = input("Enter name: ")

        book.delete_contact(name)
        
    else:
        print("Invalid choice. Please try again.")


# book.view_contact()