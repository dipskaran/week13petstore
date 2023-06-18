package pet.store.service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pet.store.controller.model.PetStoreCustomer;
import pet.store.controller.model.PetStoreData;
import pet.store.controller.model.PetStoreEmployee;
import pet.store.dao.CustomerDao;
import pet.store.dao.EmployeeDao;
import pet.store.dao.PetStoreDao;
import pet.store.entity.Customer;
import pet.store.entity.Employee;
import pet.store.entity.PetStore;

@Service
public class PetStoreService {
	@Autowired
	private PetStoreDao petStoreDao;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private CustomerDao customerDao;
	
	@Transactional(readOnly = false)
	public PetStoreData savePetStore(PetStoreData petStoreData) {
		Long petStoreId = petStoreData.getPetStoreId();
		PetStore petStore = findOrCreatePetStore(petStoreId);
		copyPetStoreFields(petStore, petStoreData);
		return new PetStoreData(petStoreDao.save(petStore));
	}

	private void copyPetStoreFields(PetStore petStore, PetStoreData petStoreData) {
			
		petStore.setPetStoreAddress(petStoreData.getPetStoreAddress());
		petStore.setPetStoreCity(petStoreData.getPetStoreCity());
		petStore.setPetStoreName(petStoreData.getPetStoreName());
		petStore.setPetStorePhone(petStoreData.getPetStorePhone());
		petStore.setPetStoreState(petStoreData.getPetStoreState());
		petStore.setPetStoreZip(petStoreData.getPetStoreZip());
		
	}

	private PetStore findOrCreatePetStore(Long petStoreId) {
		PetStore petStore;
		if (Objects.isNull(petStoreId)) {
			petStore = new PetStore();
		} else {
			petStore = findPetStoreById(petStoreId);
		}
		return petStore;
	}
	
	private PetStore findPetStoreById(Long petStoreId) {
		return petStoreDao.findById(petStoreId).orElseThrow(
				() -> new NoSuchElementException("PetStore with ID=" + petStoreId + " was not found."));
	}
	@Transactional(readOnly = false)
	public PetStoreEmployee saveEmployee(Long petStoreId, PetStoreEmployee petStoreEmployee) {
		PetStore petStore = findPetStoreById(petStoreId);
		Long employeeId = petStoreEmployee.getEmployeeId();
		Employee employee = findOrCreateEmployee(petStoreId, employeeId);
		copyEmployeeFields(employee, petStoreEmployee);
		employee.setPetStore(petStore);
		
		Employee dbEmployee = employeeDao.save(employee);
		return new PetStoreEmployee(dbEmployee);
	}

	private void copyEmployeeFields(Employee employee, PetStoreEmployee petStoreEmployee) {
		employee.setEmployeeFirstName(petStoreEmployee.getEmployeeFirstName());
		employee.setEmployeeLastName(petStoreEmployee.getEmployeeLastName());
		employee.setEmployeeJobTitle(petStoreEmployee.getEmployeeJobTitle());
		employee.setEmployeePhone(petStoreEmployee.getEmployeePhone());
		employee.setEmployeeId(petStoreEmployee.getEmployeeId());
		
	}

	private Employee findOrCreateEmployee(Long petStoreId, Long employeeId) {
		Employee employee;
		if (Objects.isNull(employeeId)) {
			employee = new Employee();
		} else {
			employee = findEmployeeById(petStoreId,employeeId);
		}
		return employee;
		
	}

	private Employee findEmployeeById(Long petStoreId, Long employeeId) {
		Employee employee=employeeDao.findById(employeeId).orElseThrow(
				() -> new NoSuchElementException("Employee with ID=" + employeeId + " was not found."));
		
		if(employee.getPetStore().getPetStoreId().equals(petStoreId)) {
			return employee;
		}else {
			throw new IllegalArgumentException("The employee "+employeeId+ 
					" does not work at pet store with id "+petStoreId);
		}
		
	}
	@Transactional(readOnly = false)
	public PetStoreCustomer saveCustomer(Long petStoreId, PetStoreCustomer petStoreCustomer) {
		PetStore petStore = findPetStoreById(petStoreId);
		Long customerId = petStoreCustomer.getCustomerId();
		Customer customer = findOrCreateCustomer(petStoreId, customerId);
		copyCustomerFields(customer, petStoreCustomer,petStore);
		Customer dbCustomer = customerDao.save(customer);
		return new PetStoreCustomer(dbCustomer);
		
	}

	private void copyCustomerFields(Customer customer, PetStoreCustomer petStoreCustomer,PetStore petStore) {
		customer.setCustomerEmail(petStoreCustomer.getCustomerEmail());
		customer.setCustomerFirstName(petStoreCustomer.getCustomerFirstName());
		customer.setCustomerId(petStoreCustomer.getCustomerId());
		customer.setCustomerLastName(petStoreCustomer.getCustomerLastName());
		Set<PetStore> petStoreSet = new HashSet<>();
		petStoreSet.add(petStore);
		customer.setPetStores(petStoreSet);
		
	}
	

	private Customer findOrCreateCustomer(Long petStoreId, Long customerId) {
		Customer customer;
		if (Objects.isNull(customerId)) {
			customer = new Customer();
		} else {
			customer = findCustomerById(petStoreId,customerId);
		}
		return customer;
	}

	private Customer findCustomerById(Long petStoreId, Long customerId) {
		Customer customer=customerDao.findById(customerId).orElseThrow(
				() -> new NoSuchElementException("Customer with ID=" + customerId + " was not found."));
		Set<PetStore> petStoresSet = customer.getPetStores();
		boolean customerFlg=false;
		for(PetStore petStore : petStoresSet) {
			if(petStore.getPetStoreId().equals(petStoreId)){
				customerFlg=true;
			}
		}
		
		if(customerFlg) {
			return customer;
		}else {
			throw new IllegalArgumentException("The customer "+customerId+ 
					" does not shop at pet store with id "+petStoreId);
		}
		
		
	}
	@Transactional(readOnly = true)
	public List<PetStoreData> retrieveAllPetStores() {
		List<PetStore> petStores = petStoreDao.findAll();
		List<PetStoreData> response = new LinkedList<>();
		
		for(PetStore petStore : petStores) {
			PetStoreData psd = new PetStoreData(petStore);
			psd.getCustomers().clear();
			psd.getEmployees().clear();
			
			response.add(psd);
		}
		
		return response;
		
	}
	
	@Transactional(readOnly = true)
	public PetStoreData retrievePetStoreById(Long petStoreId) {
		PetStore petStore = findPetStoreById(petStoreId);
		return new PetStoreData(petStore);
	}
	
	@Transactional(readOnly = false)
	public void deletePetStoreById(Long petStoreId) {
		PetStore petStore = findPetStoreById(petStoreId);
		petStoreDao.delete(petStore);
	}
	
	
}
