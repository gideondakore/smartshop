"use client";
import { useState, useEffect } from "react";
import { useAuth } from "@/lib/auth-context";
import { productApi, inventoryApi, orderApi } from "@/lib/api";
import { useRouter } from "next/navigation";
import Link from "next/link";

export default function VendorDashboard() {
  const { user, logout } = useAuth();
  const router = useRouter();
  const [products, setProducts] = useState<any[]>([]);
  const [inventories, setInventories] = useState<any[]>([]);
  const [orders, setOrders] = useState<any[]>([]);
  const [tab, setTab] = useState("products");

  useEffect(() => {
    if (!user || user.role !== "VENDOR") {
      router.push("/");
      return;
    }
    loadData();
  }, [user, router, tab]);

  const loadData = async () => {
    try {
      if (tab === "products") {
        const res = await productApi.getAll({ page: 0, size: 50 });
        setProducts(res.data.content);
      } else if (tab === "inventory") {
        const res = await inventoryApi.getAll(0, 50);
        setInventories(res.data.content);
      } else if (tab === "orders") {
        const res = await orderApi.getAll({ page: 0, size: 50 });
        setOrders(res.data.content);
      }
    } catch (err) {
      console.error("Failed to load data", err);
    }
  };

  const deleteProduct = async (id: number) => {
    if (confirm("Delete this product?")) {
      try {
        await productApi.delete(id);
        setProducts(products.filter((p) => p.id !== id));
      } catch (err) {
        alert("Failed to delete product");
      }
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
          <h1 className="text-2xl font-bold">Vendor Dashboard</h1>
          <div className="flex gap-4">
            <Link href="/" className="text-blue-600 hover:underline">
              Home
            </Link>
            <Link href="/profile" className="text-blue-600 hover:underline">
              Profile
            </Link>
            <button onClick={logout} className="text-red-600 hover:underline">
              Logout
            </button>
          </div>
        </div>
      </nav>

      <div className="max-w-7xl mx-auto px-4 py-8">
        <h2 className="text-2xl font-bold mb-6">Manage Your Store</h2>

        {/* Tab Navigation */}
        <div className="flex gap-2 mb-6">
          {["products", "inventory", "orders"].map((t) => (
            <button
              key={t}
              onClick={() => setTab(t)}
              className={`px-4 py-2 rounded ${tab === t ? "bg-blue-600 text-white" : "bg-white"}`}
            >
              {t.charAt(0).toUpperCase() + t.slice(1)}
            </button>
          ))}
        </div>

        {/* Products Tab */}
        {tab === "products" && (
          <div>
            <button
              onClick={() => router.push("/admin/products/add")}
              className="mb-4 bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
            >
              Add Product
            </button>
            <div className="bg-white rounded-lg shadow overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-100">
                  <tr>
                    <th className="px-4 py-2 text-left">ID</th>
                    <th className="px-4 py-2 text-left">Name</th>
                    <th className="px-4 py-2 text-left">SKU</th>
                    <th className="px-4 py-2 text-left">Price</th>
                    <th className="px-4 py-2 text-left">Category</th>
                    <th className="px-4 py-2 text-left">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {products.map((p) => (
                    <tr key={p.id} className="border-t">
                      <td className="px-4 py-2">{p.id}</td>
                      <td className="px-4 py-2">{p.name}</td>
                      <td className="px-4 py-2">{p.sku}</td>
                      <td className="px-4 py-2">${p.price}</td>
                      <td className="px-4 py-2">{p.categoryName}</td>
                      <td className="px-4 py-2">
                        <button
                          onClick={() =>
                            router.push(`/admin/products/edit/${p.id}`)
                          }
                          className="text-blue-600 hover:underline mr-2"
                        >
                          Edit
                        </button>
                        <button
                          onClick={() => deleteProduct(p.id)}
                          className="text-red-600 hover:underline"
                        >
                          Delete
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* Inventory Tab */}
        {tab === "inventory" && (
          <div>
            <button
              onClick={() => router.push("/admin/inventory/add")}
              className="mb-4 bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
            >
              Add Inventory
            </button>
            <div className="bg-white rounded-lg shadow overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-100">
                  <tr>
                    <th className="px-4 py-2 text-left">ID</th>
                    <th className="px-4 py-2 text-left">Product</th>
                    <th className="px-4 py-2 text-left">Quantity</th>
                    <th className="px-4 py-2 text-left">Location</th>
                    <th className="px-4 py-2 text-left">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {inventories.map((inv) => (
                    <tr key={inv.id} className="border-t">
                      <td className="px-4 py-2">{inv.id}</td>
                      <td className="px-4 py-2">{inv.productName}</td>
                      <td className="px-4 py-2">{inv.quantity}</td>
                      <td className="px-4 py-2">{inv.location}</td>
                      <td className="px-4 py-2">
                        <button
                          onClick={() =>
                            router.push(`/admin/inventory/edit/${inv.id}`)
                          }
                          className="text-blue-600 hover:underline"
                        >
                          Edit
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* Orders Tab */}
        {tab === "orders" && (
          <div className="bg-white rounded-lg shadow overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-100">
                <tr>
                  <th className="px-4 py-2 text-left">Order ID</th>
                  <th className="px-4 py-2 text-left">Customer</th>
                  <th className="px-4 py-2 text-left">Total</th>
                  <th className="px-4 py-2 text-left">Status</th>
                  <th className="px-4 py-2 text-left">Date</th>
                </tr>
              </thead>
              <tbody>
                {orders.map((order) => (
                  <tr key={order.id} className="border-t">
                    <td className="px-4 py-2">{order.id}</td>
                    <td className="px-4 py-2">{order.userName}</td>
                    <td className="px-4 py-2">${order.totalAmount}</td>
                    <td className="px-4 py-2">
                      <span
                        className={`px-2 py-1 rounded text-sm ${
                          order.status === "DELIVERED"
                            ? "bg-green-100 text-green-800"
                            : order.status === "PENDING"
                              ? "bg-yellow-100 text-yellow-800"
                              : "bg-blue-100 text-blue-800"
                        }`}
                      >
                        {order.status}
                      </span>
                    </td>
                    <td className="px-4 py-2">
                      {new Date(order.createdAt).toLocaleDateString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
