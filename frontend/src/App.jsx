import React, { useEffect, useMemo, useState } from 'react';
import {
  AlertTriangle,
  BarChart3,
  Boxes,
  ClipboardList,
  LogOut,
  PackagePlus,
  Search,
  ShieldCheck,
  Trash2,
  UserPlus,
  X
} from 'lucide-react';
import {
  createInventoryItem,
  deleteInventoryItem,
  getInventoryItems,
  login,
  register,
  updateInventoryItem
} from './api.js';

const emptyForm = {
  itemName: '',
  description: '',
  category: '',
  quantity: 0,
  thresholdQuantity: 0,
  supplier: '',
  price: ''
};

function getStoredUser() {
  const stored = localStorage.getItem('inventory_user');
  return stored ? JSON.parse(stored) : null;
}

function statusFor(item) {
  if (item.quantity === 0) return 'Out of Stock';
  if (item.quantity <= item.thresholdQuantity) return 'Low Stock';
  return 'In Stock';
}

function App() {
  const [user, setUser] = useState(getStoredUser);
  const [authMode, setAuthMode] = useState('login');
  const [authForm, setAuthForm] = useState({ username: 'admin', email: '', password: 'admin123' });
  const [authError, setAuthError] = useState('');
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [fieldErrors, setFieldErrors] = useState({});
  const [searchTerm, setSearchTerm] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('All');
  const [stockFilter, setStockFilter] = useState('All');
  const [activeView, setActiveView] = useState('dashboard');
  const [isFormOpen, setFormOpen] = useState(false);
  const [editingItem, setEditingItem] = useState(null);
  const [form, setForm] = useState(emptyForm);

  const isAdmin = user?.roles?.includes('ADMIN');

  useEffect(() => {
    if (user) {
      loadItems();
    }
  }, [user]);

  async function loadItems() {
    setLoading(true);
    setError('');
    try {
      setItems(await getInventoryItems());
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  async function handleAuth(event) {
    event.preventDefault();
    setAuthError('');
    try {
      const response = authMode === 'login'
        ? await login({ username: authForm.username, password: authForm.password })
        : await register(authForm);
      localStorage.setItem('inventory_token', response.accessToken);
      localStorage.setItem('inventory_user', JSON.stringify(response));
      setUser(response);
    } catch (err) {
      setAuthError(err.message);
    }
  }

  function logout() {
    localStorage.removeItem('inventory_token');
    localStorage.removeItem('inventory_user');
    setUser(null);
    setItems([]);
  }

  function openCreateForm() {
    setEditingItem(null);
    setForm(emptyForm);
    setFieldErrors({});
    setFormOpen(true);
  }

  function openEditForm(item) {
    setEditingItem(item);
    setForm({
      itemName: item.itemName || '',
      description: item.description || '',
      category: item.category || '',
      quantity: item.quantity ?? 0,
      thresholdQuantity: item.thresholdQuantity ?? 0,
      supplier: item.supplier || '',
      price: item.price ?? ''
    });
    setFieldErrors({});
    setFormOpen(true);
  }

  async function saveItem(event) {
    event.preventDefault();
    setFieldErrors({});
    setError('');

    const payload = {
      ...form,
      quantity: Number(form.quantity),
      thresholdQuantity: Number(form.thresholdQuantity),
      price: Number(form.price)
    };

    try {
      if (editingItem) {
        await updateInventoryItem(editingItem.id, payload);
      } else {
        await createInventoryItem(payload);
      }
      setFormOpen(false);
      await loadItems();
    } catch (err) {
      setError(err.message);
      setFieldErrors(err.fieldErrors || {});
    }
  }

  async function removeItem(item) {
    if (!window.confirm(`Delete ${item.itemName}?`)) return;
    setError('');
    try {
      await deleteInventoryItem(item.id);
      await loadItems();
    } catch (err) {
      setError(err.message);
    }
  }

  const categories = useMemo(() => ['All', ...new Set(items.map((item) => item.category).filter(Boolean))], [items]);

  const filteredItems = useMemo(() => {
    return items.filter((item) => {
      const text = `${item.itemName} ${item.category} ${item.supplier}`.toLowerCase();
      const matchesSearch = text.includes(searchTerm.toLowerCase());
      const matchesCategory = categoryFilter === 'All' || item.category === categoryFilter;
      const matchesStock = stockFilter === 'All' || statusFor(item) === stockFilter;
      return matchesSearch && matchesCategory && matchesStock;
    });
  }, [items, searchTerm, categoryFilter, stockFilter]);

  const lowStockItems = items.filter((item) => item.quantity <= item.thresholdQuantity);
  const inventoryValue = items.reduce((total, item) => total + Number(item.price || 0) * Number(item.quantity || 0), 0);

  if (!user) {
    return (
      <AuthPage
        authMode={authMode}
        setAuthMode={setAuthMode}
        authForm={authForm}
        setAuthForm={setAuthForm}
        authError={authError}
        onSubmit={handleAuth}
      />
    );
  }

  return (
    <div className="appShell">
      <aside className="sidebar">
        <div className="brand">
          <div className="brandIcon">IM</div>
          <div>
            <strong>Inventory Management</strong>
            <span>Retail operations</span>
          </div>
        </div>

        <nav className="nav">
          <button className={activeView === 'dashboard' ? 'active' : ''} onClick={() => setActiveView('dashboard')}>
            <BarChart3 size={18} /> Dashboard
          </button>
          <button className={activeView === 'inventory' ? 'active' : ''} onClick={() => setActiveView('inventory')}>
            <Boxes size={18} /> Inventory
          </button>
          <button className={activeView === 'alerts' ? 'active' : ''} onClick={() => setActiveView('alerts')}>
            <AlertTriangle size={18} /> Alerts
          </button>
          <button className={activeView === 'orders' ? 'active' : ''} onClick={() => setActiveView('orders')}>
            <ClipboardList size={18} /> Orders
          </button>
        </nav>
      </aside>

      <main className="workspace">
        <header className="topbar">
          <div className="searchBox">
            <Search size={18} />
            <input value={searchTerm} onChange={(event) => setSearchTerm(event.target.value)} placeholder="Search item, supplier, category" />
          </div>
          <div className="account">
            <span className="pill mutedPill">{user.email}</span>
            <span className="pill adminPill">{user.roles?.join(', ')}</span>
            <button className="iconButton" onClick={logout} title="Logout"><LogOut size={18} /></button>
          </div>
        </header>

        <section className="content">
          {error && <div className="alert errorAlert">{error}</div>}

          {activeView === 'dashboard' && (
            <Dashboard items={items} lowStockItems={lowStockItems} inventoryValue={inventoryValue} onAdd={openCreateForm} isAdmin={isAdmin} />
          )}

          {activeView === 'inventory' && (
            <InventoryView
              items={filteredItems}
              categories={categories}
              categoryFilter={categoryFilter}
              setCategoryFilter={setCategoryFilter}
              stockFilter={stockFilter}
              setStockFilter={setStockFilter}
              loading={loading}
              onAdd={openCreateForm}
              onEdit={openEditForm}
              onDelete={removeItem}
              isAdmin={isAdmin}
            />
          )}

          {activeView === 'alerts' && <AlertsView items={lowStockItems} />}
          {activeView === 'orders' && <PlaceholderView />}
        </section>
      </main>

      {isFormOpen && (
        <InventoryModal
          form={form}
          setForm={setForm}
          fieldErrors={fieldErrors}
          editingItem={editingItem}
          onClose={() => setFormOpen(false)}
          onSubmit={saveItem}
        />
      )}
    </div>
  );
}

function AuthPage({ authMode, setAuthMode, authForm, setAuthForm, authError, onSubmit }) {
  return (
    <div className="authPage">
      <section className="authPanel">
        <div className="authIntro">
          <div className="brandIcon large">IM</div>
          <h1>Inventory Management</h1>
          <p>Secure dashboard for stock, supplier, and low-inventory workflows.</p>
        </div>

        <form className="authCard" onSubmit={onSubmit}>
          <div>
            <h2>{authMode === 'login' ? 'Sign in' : 'Create user'}</h2>
          </div>

          {authError && <div className="alert errorAlert">{authError}</div>}

          <label>
            Username
            <input value={authForm.username} onChange={(event) => setAuthForm({ ...authForm, username: event.target.value })} />
          </label>

          {authMode === 'register' && (
            <label>
              Email
              <input type="email" value={authForm.email} onChange={(event) => setAuthForm({ ...authForm, email: event.target.value })} />
            </label>
          )}

          <label>
            Password
            <input type="password" value={authForm.password} onChange={(event) => setAuthForm({ ...authForm, password: event.target.value })} />
          </label>

          <button className="primaryButton" type="submit">{authMode === 'login' ? 'Login' : 'Register'}</button>
          <button className="textOnly" type="button" onClick={() => setAuthMode(authMode === 'login' ? 'register' : 'login')}>
            {authMode === 'login' ? 'Create a new user' : 'Back to login'}
          </button>
        </form>
      </section>
    </div>
  );
}

function Dashboard({ items, lowStockItems, inventoryValue, onAdd, isAdmin }) {
  return (
    <>
      <PageHeader
        title="Dashboard"
        subtitle="Stock, supplier, order, and alert overview."
        action={isAdmin && <button className="primaryButton compact" onClick={onAdd}><PackagePlus size={18} /> Add item</button>}
      />

      <div className="metrics">
        <Metric label="Total items" value={items.length} />
        <Metric label="Low stock" value={lowStockItems.length} tone="warn" />
        <Metric label="Out of stock" value={items.filter((item) => item.quantity === 0).length} tone="danger" />
        <Metric label="Categories" value={new Set(items.map((item) => item.category)).size} />
        <Metric label="Inventory value" value={`Rs ${inventoryValue.toLocaleString('en-IN')}`} />
      </div>

      <div className="splitGrid">
        <div className="panel">
          <div className="panelHeader"><h2>Recent Inventory</h2></div>
          <InventoryTable items={items.slice(0, 5)} isAdmin={false} />
        </div>
        <div className="panel">
          <div className="panelHeader"><h2>Low Stock Preview</h2></div>
          <div className="feed">
            {lowStockItems.length === 0 && <p className="empty">No low-stock items.</p>}
            {lowStockItems.slice(0, 5).map((item) => (
              <div className="feedItem" key={item.id}>
                <strong>{item.itemName}</strong>
                <span>{item.quantity} available, threshold {item.thresholdQuantity}</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </>
  );
}

function InventoryView(props) {
  return (
    <>
      <PageHeader
        title="Inventory Items"
        subtitle="Create, view, update, delete, search, and filter inventory records."
        action={props.isAdmin && <button className="primaryButton compact" onClick={props.onAdd}><PackagePlus size={18} /> Add item</button>}
      />

      <div className="filters">
        <select value={props.categoryFilter} onChange={(event) => props.setCategoryFilter(event.target.value)}>
          {props.categories.map((category) => <option key={category}>{category}</option>)}
        </select>
        <select value={props.stockFilter} onChange={(event) => props.setStockFilter(event.target.value)}>
          <option>All</option>
          <option>In Stock</option>
          <option>Low Stock</option>
          <option>Out of Stock</option>
        </select>
      </div>

      <div className="panel">
        <div className="panelHeader">
          <h2>Inventory Table</h2>
          <span>{props.loading ? 'Loading...' : `${props.items.length} records`}</span>
        </div>
        <InventoryTable {...props} />
      </div>
    </>
  );
}

function InventoryTable({ items, isAdmin, onEdit, onDelete }) {
  return (
    <div className="tableWrap">
      <table>
        <thead>
          <tr>
            <th>Item</th>
            <th>Category</th>
            <th>Qty</th>
            <th>Threshold</th>
            <th>Price</th>
            <th>Supplier</th>
            <th>Status</th>
            {isAdmin && <th>Actions</th>}
          </tr>
        </thead>
        <tbody>
          {items.length === 0 && (
            <tr><td colSpan={isAdmin ? 8 : 7} className="empty">No inventory items found.</td></tr>
          )}
          {items.map((item) => (
            <tr key={item.id}>
              <td><strong>{item.itemName}</strong><small>{item.description}</small></td>
              <td>{item.category}</td>
              <td>{item.quantity}</td>
              <td>{item.thresholdQuantity}</td>
              <td>Rs {Number(item.price).toLocaleString('en-IN')}</td>
              <td>{item.supplier}</td>
              <td><StatusBadge status={statusFor(item)} /></td>
              {isAdmin && (
                <td>
                  <div className="rowActions">
                    <button className="linkButton" onClick={() => onEdit(item)}>Edit</button>
                    <button className="dangerButton" onClick={() => onDelete(item)}><Trash2 size={16} /></button>
                  </div>
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function AlertsView({ items }) {
  return (
    <>
      <PageHeader title="Low Stock Alerts" subtitle="Items where quantity is at or below threshold." />
      <div className="panel">
        <InventoryTable items={items} isAdmin={false} />
      </div>
    </>
  );
}

function PlaceholderView() {
  return (
    <>
      <PageHeader title="Orders" subtitle="Order processing belongs to Milestone 2." />
      <div className="panel placeholder">
        <ClipboardList size={36} />
        <h2>Order module coming next</h2>
        <p>Order confirmation, status updates, history, and supplier performance reports will live here.</p>
      </div>
    </>
  );
}

function InventoryModal({ form, setForm, fieldErrors, editingItem, onClose, onSubmit }) {
  return (
    <div className="modalOverlay">
      <form className="modal" onSubmit={onSubmit}>
        <div className="modalHeader">
          <div>
            <h2>{editingItem ? 'Update inventory item' : 'Add inventory item'}</h2>
            <p>Fields match the Spring Boot InventoryItemRequest DTO.</p>
          </div>
          <button className="iconButton" type="button" onClick={onClose}><X size={18} /></button>
        </div>

        <div className="formGrid">
          <Input label="Item name" name="itemName" form={form} setForm={setForm} error={fieldErrors.itemName} />
          <Input label="Category" name="category" form={form} setForm={setForm} error={fieldErrors.category} />
          <Input label="Description" name="description" form={form} setForm={setForm} error={fieldErrors.description} full textarea />
          <Input label="Quantity" name="quantity" type="number" form={form} setForm={setForm} error={fieldErrors.quantity} />
          <Input label="Threshold quantity" name="thresholdQuantity" type="number" form={form} setForm={setForm} error={fieldErrors.thresholdQuantity} />
          <Input label="Supplier" name="supplier" form={form} setForm={setForm} error={fieldErrors.supplier} />
          <Input label="Price" name="price" type="number" form={form} setForm={setForm} error={fieldErrors.price} />
        </div>

        <div className="modalFooter">
          <button className="secondaryButton" type="button" onClick={onClose}>Cancel</button>
          <button className="primaryButton" type="submit">{editingItem ? 'Update item' : 'Create item'}</button>
        </div>
      </form>
    </div>
  );
}

function Input({ label, name, type = 'text', form, setForm, error, full, textarea }) {
  const common = {
    value: form[name],
    onChange: (event) => setForm({ ...form, [name]: event.target.value })
  };
  return (
    <label className={full ? 'full' : ''}>
      {label}
      {textarea ? <textarea {...common} /> : <input type={type} {...common} />}
      {error && <span className="fieldError">{error}</span>}
    </label>
  );
}

function PageHeader({ title, subtitle, action }) {
  return (
    <div className="pageHeader">
      <div>
        <h1>{title}</h1>
        <p>{subtitle}</p>
      </div>
      {action}
    </div>
  );
}

function Metric({ label, value, tone }) {
  return (
    <div className={`metric ${tone || ''}`}>
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function StatusBadge({ status }) {
  const className = status === 'In Stock' ? 'ok' : status === 'Low Stock' ? 'warn' : 'danger';
  return <span className={`status ${className}`}>{status}</span>;
}

export default App;



