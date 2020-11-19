package io.github.zyngjaku.tmsfrontend.ui.employee;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import io.github.zyngjaku.tmsfrontend.R;
import io.github.zyngjaku.tmsfrontend.entity.User;
import io.github.zyngjaku.tmsfrontend.entity.Vehicle;
import io.github.zyngjaku.tmsfrontend.utils.Utils;

public class EmployeeListViewAdapter extends ArrayAdapter<User> implements View.OnClickListener {
    private Context context;
    private ArrayList<User> listOfUsers;
    private ArrayList<User> listOfUsersFiltered;

    private static class ViewHolder {
        TextView name;
        TextView role;
        ImageView avatar;
        ImageButton editImageButton;
        ImageButton deleteImageButton;
    }

    public EmployeeListViewAdapter(ArrayList<User> data, Context context) {
        super(context, R.layout.employees_row, data);
        this.context = context;
        this.listOfUsers = data;
        this.listOfUsersFiltered = data;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Object object = getItem(position);
        User users = (User) object;
    }

    @Override
    public int getCount() {
        return listOfUsersFiltered.size();
    }

    @Override
    public User getItem(int position) {
        return listOfUsersFiltered.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final User user = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.employees_row, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.name = convertView.findViewById(R.id.nameTextView);
            viewHolder.role = convertView.findViewById(R.id.roleTextView);
            viewHolder.avatar = convertView.findViewById(R.id.avatarImageView);
            viewHolder.editImageButton = convertView.findViewById(R.id.editImageButton);
            viewHolder.deleteImageButton = convertView.findViewById(R.id.deleteImageButton);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(user.getFirstName() + " " + user.getLastName());
        viewHolder.role.setText(user.getRole().toString());
        viewHolder.avatar.setImageBitmap(user.getAvatarBitmap());
        viewHolder.editImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editEmployeeDialog(user);
            }
        });
        viewHolder.deleteImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                removeEmployeeDialog(user);
            }
        });

        return convertView;
    }

    private void editEmployeeDialog(final User user) {
        final Dialog dialog = Utils.createDialog((Activity) context, R.layout.dialog_template, true, "Edit employee", R.layout.vehicle_dialog_edit);

        Button dialogSaveButton = (Button) dialog.findViewById(R.id.submitButton);
        dialogSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Employee has been successfully edited!", Toast.LENGTH_SHORT).show();
                //TODO: API & Locally - Update vehicle and check if there is any difference
                dialog.dismiss();
            }
        });

        Button dialogCancelButton = (Button) dialog.findViewById(R.id.cancelButton);
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void removeEmployeeDialog(User user) {
        final Dialog dialog = Utils.createDialog((Activity) context, R.layout.dialog_template, true, "Do you want to delete employee?");

        Button dialogSaveButton = (Button) dialog.findViewById(R.id.submitButton);
        dialogSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Employee has been removed!", Toast.LENGTH_SHORT).show();
                //TODO: API & Locally - Remove vehicle
                dialog.dismiss();
            }
        });

        Button dialogCancelButton = (Button) dialog.findViewById(R.id.cancelButton);
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listOfUsersFiltered = (ArrayList<User>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<User> filteredUsers = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    results.count = listOfUsers.size();
                    results.values = listOfUsers;
                } else {
                    constraint = constraint.toString().toLowerCase();

                    for (int i = 0; i < listOfUsers.size(); i++) {
                        String data = listOfUsers.get(i).getFirstName() + " " + listOfUsers.get(i).getLastName();

                        if (data.toLowerCase().contains(constraint.toString())) {
                            filteredUsers.add(listOfUsers.get(i));
                        }
                    }

                    results.count = filteredUsers.size();
                    results.values = filteredUsers;
                }

                return results;
            }
        };

        return filter;
    }

}
