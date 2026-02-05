"use client";
import { useState, useEffect } from "react";
import { useAuth } from "@/lib/auth-context";
import { userApi } from "@/lib/api";
import { useRouter } from "next/navigation";
import Link from "next/link";

export default function AdminDashboard() {
  const { user, logout } = useAuth();
  const router = useRouter();
  const [users, setUsers] = useState<any[]>([]);
  const [vendors, setVendors] = useState<any[]>([]);
  const [customers, setCustomers] = useState<any[]>([]);
  const [stats, setStats] = useState({
    totalUsers: 0,
    totalVendors: 0,
    totalCustomers: 0,
  });

  useEffect(() => {
    if (!user || user.role !== "ADMIN") {
      router.push("/");
      return;
    }
    loadUsers();
  }, [user, router]);

  const loadUsers = async () => {
    try {
      const res = await userApi.getAllUsers(0, 100);
      const allUsers = res.data.content;
      setUsers(allUsers);

      const vendorsList = allUsers.filter((u: any) => u.role === "VENDOR");
      const customersList = allUsers.filter((u: any) => u.role === "CUSTOMER");

      setVendors(vendorsList);
      setCustomers(customersList);
      setStats({
        totalUsers: allUsers.length,
        totalVendors: vendorsList.length,
        totalCustomers: customersList.length,
      });
    } catch (err) {
      console.error("Failed to load users", err);
    }
  };

  const deleteUser = async (id: number) => {
    if (confirm("Are you sure you want to delete this user?")) {
      try {
        await userApi.deleteUser(id);
        loadUsers();
      } catch (err) {
        alert("Failed to delete user");
      }
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
          <h1 className="text-2xl font-bold">Admin Dashboard</h1>
          <div className="flex gap-4">
            <Link href="/" className="text-blue-600 hover:underline">
              Home
            </Link>
            <Link href="/admin" className="text-blue-600 hover:underline">
              Manage Store
            </Link>
            <button onClick={logout} className="text-red-600 hover:underline">
              Logout
            </button>
          </div>
        </div>
      </nav>

      <div className="max-w-7xl mx-auto px-4 py-8">
        {/* Statistics Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-gray-500 text-sm font-semibold mb-2">
              Total Users
            </h3>
            <p className="text-3xl font-bold text-blue-600">
              {stats.totalUsers}
            </p>
          </div>
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-gray-500 text-sm font-semibold mb-2">
              Total Vendors
            </h3>
            <p className="text-3xl font-bold text-green-600">
              {stats.totalVendors}
            </p>
          </div>
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-gray-500 text-sm font-semibold mb-2">
              Total Customers
            </h3>
            <p className="text-3xl font-bold text-purple-600">
              {stats.totalCustomers}
            </p>
          </div>
        </div>

        {/* Vendors Section */}
        <div className="mb-8">
          <h2 className="text-2xl font-bold mb-4">Vendors Management</h2>
          <div className="bg-white rounded-lg shadow overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-100">
                <tr>
                  <th className="px-4 py-2 text-left">ID</th>
                  <th className="px-4 py-2 text-left">Name</th>
                  <th className="px-4 py-2 text-left">Email</th>
                  <th className="px-4 py-2 text-left">Actions</th>
                </tr>
              </thead>
              <tbody>
                {vendors.map((vendor) => (
                  <tr key={vendor.id} className="border-t">
                    <td className="px-4 py-2">{vendor.id}</td>
                    <td className="px-4 py-2">
                      {vendor.firstName} {vendor.lastName}
                    </td>
                    <td className="px-4 py-2">{vendor.email}</td>
                    <td className="px-4 py-2">
                      <button
                        onClick={() => deleteUser(vendor.id)}
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

        {/* Customers Section */}
        <div>
          <h2 className="text-2xl font-bold mb-4">Customers Management</h2>
          <div className="bg-white rounded-lg shadow overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-100">
                <tr>
                  <th className="px-4 py-2 text-left">ID</th>
                  <th className="px-4 py-2 text-left">Name</th>
                  <th className="px-4 py-2 text-left">Email</th>
                  <th className="px-4 py-2 text-left">Actions</th>
                </tr>
              </thead>
              <tbody>
                {customers.map((customer) => (
                  <tr key={customer.id} className="border-t">
                    <td className="px-4 py-2">{customer.id}</td>
                    <td className="px-4 py-2">
                      {customer.firstName} {customer.lastName}
                    </td>
                    <td className="px-4 py-2">{customer.email}</td>
                    <td className="px-4 py-2">
                      <button
                        onClick={() => deleteUser(customer.id)}
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
      </div>
    </div>
  );
}
