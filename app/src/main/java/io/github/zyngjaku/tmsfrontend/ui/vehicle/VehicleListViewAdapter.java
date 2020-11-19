package io.github.zyngjaku.tmsfrontend.ui.vehicle;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import io.github.zyngjaku.tmsfrontend.R;
import io.github.zyngjaku.tmsfrontend.entity.Vehicle;
import io.github.zyngjaku.tmsfrontend.utils.Utils;

public class VehicleListViewAdapter extends ArrayAdapter<Vehicle> implements View.OnClickListener {
    private Context context;
    private ArrayList<Vehicle> listOfVehicles;
    private ArrayList<Vehicle> listOfVehiclesFiltered;

    private static class ViewHolder {
        TextView nameTextView;
        TextView registrationTextView;
        ImageButton editImageButton;
        ImageButton deleteImageButton;
    }

    public VehicleListViewAdapter(ArrayList<Vehicle> data, Context context) {
        super(context, R.layout.vehicle_row, data);
        this.context = context;
        this.listOfVehicles = data;
        this.listOfVehiclesFiltered = data;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Object object = getItem(position);
        Vehicle vehicle = (Vehicle) object;
    }

    @Override
    public int getCount() {
        return listOfVehiclesFiltered.size();
    }

    @Override
    public Vehicle getItem(int position) {
        return listOfVehiclesFiltered.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Vehicle vehicle = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.vehicle_row, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.nameTextView = convertView.findViewById(R.id.nameTextView);
            viewHolder.registrationTextView = convertView.findViewById(R.id.registrationTextView);
            viewHolder.editImageButton = convertView.findViewById(R.id.editImageButton);
            viewHolder.deleteImageButton = convertView.findViewById(R.id.deleteImageButton);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.nameTextView.setText(vehicle.getName());
        viewHolder.registrationTextView.setText(vehicle.getRegistration());
        viewHolder.editImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editVehicleDialog(vehicle);
            }
        });
        viewHolder.deleteImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                removeVehicleDialog(vehicle);
            }
        });

        return convertView;
    }

    private void editVehicleDialog(final Vehicle vehicle) {
        final Dialog dialog = Utils.createDialog((Activity) context, R.layout.dialog_template, true, "Edit vehicle", R.layout.vehicle_dialog_edit);

        TextView nameEditText = dialog.findViewById(R.id.nameEditText);
        nameEditText.setText(vehicle.getName());

        TextView registrationEditText = dialog.findViewById(R.id.registrationEditText);
        registrationEditText.setText(vehicle.getRegistration());

        final TextView dateEditText = dialog.findViewById(R.id.dateEditText);
        if (vehicle.getReviewDate() != null) {
            dateEditText.setText(vehicle.getReviewDate().getDayOfMonth() + "-" + (vehicle.getReviewDate().getMonthValue() + 1) + "-" + vehicle.getReviewDate().getYear());
        }

        ImageButton calendarImageButton = dialog.findViewById(R.id.calendarImageButton);
        calendarImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year, month, day;
                if (vehicle.getReviewDate() != null) {
                    year = vehicle.getReviewDate().getYear();
                    month = vehicle.getReviewDate().getMonthValue();
                    day = vehicle.getReviewDate().getDayOfMonth();
                } else {
                    Calendar calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
                }

                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDateSet(DatePicker view, int year_, int month_, int day_) {
                                dateEditText.setText((day_ < 10? "0" : "") + day_ + "-" + ((month_ + 1) < 10? "0" : "") + (month_ + 1)  + "-" + year_);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        Button dialogSaveButton = (Button) dialog.findViewById(R.id.submitButton);
        dialogSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Vehicle has been successfully edited!", Toast.LENGTH_SHORT).show();
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

    private void removeVehicleDialog(Vehicle vehicle) {
        final Dialog dialog = Utils.createDialog((Activity) context, R.layout.dialog_template, true, "Do you want to delete vehicle?");

        Button dialogSaveButton = (Button) dialog.findViewById(R.id.submitButton);
        dialogSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Vehicle has been removed!", Toast.LENGTH_SHORT).show();
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
                listOfVehiclesFiltered = (ArrayList<Vehicle>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Vehicle> filteredUsers = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    results.count = listOfVehicles.size();
                    results.values = listOfVehicles;
                } else {
                    constraint = constraint.toString().toLowerCase();

                    for (int i = 0; i < listOfVehicles.size(); i++) {
                        String data = listOfVehicles.get(i).getName();

                        if (data.toLowerCase().contains(constraint.toString())) {
                            filteredUsers.add(listOfVehicles.get(i));
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
